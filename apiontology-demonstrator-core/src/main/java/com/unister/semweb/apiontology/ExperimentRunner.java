package com.unister.semweb.apiontology;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clarkparsia.owlapiv3.XSD;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.unister.semweb.apiontology.data.BooleanVisitor;
import com.unister.semweb.apiontology.data.OntologyUtils;
import com.unister.semweb.apiontology.demonstrator.api.exchange.ConfigurationObject;
import com.unister.semweb.apiontology.demonstrator.api.exchange.ConfigurationObject.Builder;
import com.unister.semweb.apiontology.demonstrator.api.exchange.Constraint;
import com.unister.semweb.apiontology.demonstrator.api.exchange.Equivalence;
import com.unister.semweb.apiontology.demonstrator.api.exchange.ExperimentInput;
import com.unister.semweb.apiontology.demonstrator.api.owl.GD;
import com.unister.semweb.apiontology.util.Constants;
import com.unister.semweb.apiontology.util.Utils;

public class ExperimentRunner {

	private static final transient Logger logger = LoggerFactory.getLogger(ExperimentRunner.class);
	@Resource
	private ServiceDiscovery discovery;
	@Resource
	private OntologyUtils dao;
	@Resource
	private OWLOntologyManager manager;

	public ExperimentInput getExperimentInput() {
		ExperimentInput.Builder builder = ExperimentInput.builder();
		Set<IRI> parameters = dao.parameters();
		for (IRI parameter : parameters) {
			builder.value(shortIri(parameter));
		}
		return builder.build();
	}

	public ConfigurationObject getConfiguration(OWLOntology ontology) {
		Builder builder = ConfigurationObject.builder();
		Set<IRI> parameters = dao.parameters();
		Multimap<String, String> multimap = HashMultimap.create();
		for (IRI iri : parameters) {
			String parameter = shortIri(iri);
			multimap.put(dao.webservice(iri), parameter);
			Set<IRI> eqParameters = Sets.newHashSet(parameters);
			eqParameters.remove(iri);
			com.unister.semweb.apiontology.demonstrator.api.exchange.Equivalence.Builder eqBuilder = Equivalence
					.builder();
			for (IRI eq : eqParameters) {
				eqBuilder.eqParameter(shortIri(eq));
			}
			builder.equivalence(eqBuilder.parameter(parameter).build());
		}

		for (String key : multimap.keySet()) {
			if (key != null) {
				builder.constraint((Constraint.builder().webService(key).parameters(multimap.get(key)).build()));
			}
		}
		builder.standardDatamodel(manager.getOntology(Constants.GD_ONTOLOGY), dao.getPrefixes());
		if (ontology != null) {
			builder.datamodel(ontology, dao.getPrefixes());
		} else {
			builder.datamodel(manager.getOntology(Constants.ADM_ONTOLOGY), dao.getPrefixes());
		}
		return builder.build();
	}

	public ConfigurationObject getConfiguration() {
		return this.getConfiguration(null);
	}

	private String shortIri(IRI iri) {
		if (iri.getNamespace() != null) {
			if (dao.getPrefixes().containsKey(iri.getNamespace())) {
				return dao.getPrefixes().get(iri.getNamespace()) + ":" + iri.getShortForm();
			}
		}
		return iri.toString();
	}

	private IRI expandIri(String iri) {
		String[] split = iri.split(":");
		String key = split[0];
		String suffix = split[1];
		BiMap<String, String> inversedMap = dao.getPrefixes().inverse();
		if (inversedMap.containsKey(key)) {
			return IRI.create(inversedMap.get(key), suffix);
		}
		return IRI.create(iri);
	}

	private List<IRI> expandIris(Collection<String> iris) {
		List<IRI> expanded = Lists.newLinkedList();
		for (String iri : iris) {
			expanded.add(this.expandIri(iri));
		}
		return expanded;
	}

	public OWLOntology addWebService(String service) {
		discovery.discover(service);
		return manager.getOntology(Constants.ADM_ONTOLOGY);
	}

	public OWLOntology addMandatoryParam(String webservice, Collection<String> params) {
		dao.addConstraints(webservice, expandIris(params));
		return manager.getOntology(Constants.ADM_ONTOLOGY);
	}

	public OWLOntology addEquivalence(String p1, String p2) {
		dao.addEquivalent(expandIri(p1), expandIri(p2));
		return manager.getOntology(Constants.ADM_ONTOLOGY);
	}

	public Response runExperiment(ExperimentInput input) throws OWLOntologyStorageException {
		int run = 0;
		OWLOntology baseOntology = manager.getOntology(Constants.ADM_ONTOLOGY);
		try {

			baseOntology.getOWLOntologyManager().saveOntology(baseOntology, IRI.create(new File("adm")));
			boolean solvable = true;
			long ts = Calendar.getInstance().getTimeInMillis();
			List<IRI> parameters = Lists.newLinkedList();
			List<IRI> parametersClasses = Lists.newLinkedList();

			while (solvable) {

				OWLOntology ontology = manager.createOntology(IRI.create(GD.NAMESPACE, "experiment-" + ts + "_" + run),
						Sets.newHashSet(baseOntology));
				ontology.getOWLOntologyManager().saveOntology(ontology, IRI.create(new File("testcontsraints")));
				OntologyUtils ontologyUtils = new OntologyUtils(ontology, dao.getPrefixes());
				RunningOntology ro = new RunningOntology(ontology, ontologyUtils);
				ro.init();
				for (Entry<String, String> param : input.getValues().entrySet()) {
					if (param.getValue() != null) {
						parameters.add(ro.addParam(expandIri(param.getKey()), param.getValue()));
					}
				}

				for (IRI iri : parameters) {
					parametersClasses.addAll(ro.getTypes(iri));
				}
				OWLNamedIndividual service = ro.addRequest(parameters);

				Set<OWLClass> classes = ro.getClasses(service);
				if (classes == null || classes.isEmpty()) {
					solvable = false;
				}

				ontology.getOWLOntologyManager().saveOntology(ontology, IRI.create(new File("torun.rdf")));

				solvable = false;
				for (Iterator<OWLClass> it = classes.iterator(); it.hasNext();) {
					OWLClass serviceClass = it.next();

					List<IRI> returnParams = dao.getResultParams(serviceClass.getIRI().toString());

					if (!parametersClasses.containsAll(returnParams)) {
						Object[] results = ro.invoke(service, serviceClass);
						solvable = processResults(results, ontology, serviceClass.getIRI(), ontologyUtils, input);
					}
				}
				ontology.getOWLOntologyManager().removeAxioms(ontology, ontology.getAxioms(service));
				baseOntology = ontology;
				run++;
			}
		} catch (OWLOntologyCreationException | MalformedURLException e) {
			logger.error("Error on service discovery", e);
		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException | NoSuchMethodException
				| SecurityException | IllegalArgumentException | InvocationTargetException e) {
			logger.error("Error on running experiment ", e);
		}
		input.setExecutionRuns(run - 1);
		return new Response(input, this.getConfiguration(baseOntology));
	}

	private boolean processResults(Object[] results, OWLOntology ontology, IRI serviceClass,
			OntologyUtils ontologyUtils, ExperimentInput input)
					throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		boolean hasResult = false;
		for (Object result : results) {

			if (result.getClass().equals(String.class)) {
				processLiteral(ontology, serviceClass, ontologyUtils, input, result);
			} else {

				// StringWriter writer = new StringWriter();
				// JAXB.marshal(result, writer);
				// List<IRI> iris = getOutputParams(ontology, serviceClass,
				// ontologyUtils, XSD.BASE_64_BINARY);
				// for (IRI iri : iris) {
				// String resultAsString = writer.toString();
				// input.getValues().put(iri.toString(), (String) result);
				// input.getIsNew().put(iri.toString(), true);
				// logger.info("Result: {}\ntype: {}", resultAsString, iri);
				// }
				//
				for (Method m : result.getClass().getMethods()) {
					if (m.getName().startsWith("get")) {
						OWLClassExpression param = ontologyUtils
								.getParam(Utils.classMethod2Literal(result.getClass(), m));
						Object value = m.invoke(result);
						if (param !=null && value != null) {
							String iri = shortIri(param.asOWLClass().getIRI());
							input.getValues().put(iri, value.toString());
							input.getIsNew().put(iri, true);
							logger.info("Result: {}\ntype: {}", result, iri);
							hasResult = true;
						}
					}
				}
			}
		}
		return hasResult;
	}

	private void processLiteral(OWLOntology ontology, IRI serviceClass, OntologyUtils ontologyUtils,
			ExperimentInput input, Object result) {
		List<IRI> iris = getOutputParams(ontology, serviceClass, ontologyUtils, XSD.STRING);
		for (IRI iri : iris) {
			input.getValues().put(shortIri(iri), (String) result);
			input.getIsNew().put(shortIri(iri), true);
			logger.info("Result: {}\ntype: {}", result, iri);
		}
	}

	private List<IRI> getOutputParams(OWLOntology ontology, IRI serviceClass, OntologyUtils ontologyUtils,
			OWLDatatype datatype) {
		OWLDataFactory owlDataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAnnotationProperty webService = owlDataFactory.getOWLAnnotationProperty(GD.WEB_SERVICE_PROPERTY);
		OWLAnnotationProperty isInput = owlDataFactory.getOWLAnnotationProperty(GD.IS_INPUT);
		List<IRI> iris = Lists.newLinkedList();
		BooleanVisitor visitor = new BooleanVisitor();

		OWLClass paramClass = ontologyUtils.getParamByType(datatype);

		for (OWLSubClassOfAxiom params : ontology.getSubClassAxiomsForSuperClass(paramClass)) {
			IRI iri = params.getSubClass().asOWLClass().getIRI();
			boolean parameter = false;
			boolean input = false;
			for (OWLAnnotationAssertionAxiom axiom : ontology.getAnnotationAssertionAxioms(iri)) {
				if (axiom.getProperty().equals(webService)) {
					if (axiom.getValue().equals(serviceClass)) {
						parameter = true;
					}
				} else if (axiom.getProperty().equals(isInput)) {
					axiom.getValue().accept(visitor);
					input = visitor.getResult();
				}
			}
			if (parameter && !input) {
				iris.add(iri);
			}
		}

		Collections2.filter(iris, Predicates.and(Predicates.not(new IsInput(ontology)),
				new SubClassOf(ontology, ontologyUtils.getParamByType(datatype))));
		return iris;
	}

	public static class IsInput implements Predicate<IRI> {

		private OWLOntology ontology;
		private OWLDataFactory owlDataFactory;

		/**
		 * @param ontology
		 */
		public IsInput(OWLOntology ontology) {
			this.ontology = ontology;
			this.owlDataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
		}

		@Override
		public boolean apply(IRI input) {
			boolean isInput = ontology.containsAxiom(owlDataFactory.getOWLDataPropertyAssertionAxiom(
					owlDataFactory.getOWLDataProperty(GD.IS_INPUT), owlDataFactory.getOWLNamedIndividual(input), true));
			return isInput;
		}

	}

	public static class SubClassOf implements Predicate<IRI> {

		private final OWLOntology ontology;
		private final OWLDataFactory owlDataFactory;
		private final OWLClass superClass;

		/**
		 * @param ontology
		 */
		public SubClassOf(OWLOntology ontology, OWLClass superClass) {
			this.ontology = ontology;
			this.owlDataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
			this.superClass = superClass;
		}

		@Override
		public boolean apply(IRI input) {
			return ontology
					.containsAxiom(owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLClass(input), superClass));
		}

	}

	public static class Response {
		private final ExperimentInput input;
		private final ConfigurationObject configuration;

		/**
		 * @param input
		 * @param configuration
		 */
		public Response(final ExperimentInput input, final ConfigurationObject configuration) {
			this.input = input;
			this.configuration = configuration;
		}

		public ExperimentInput getInput() {
			return input;
		}

		public ConfigurationObject getConfiguration() {
			return configuration;
		}

	}

}
