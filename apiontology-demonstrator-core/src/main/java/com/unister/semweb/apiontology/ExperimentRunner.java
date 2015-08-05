package com.unister.semweb.apiontology;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.bind.JAXB;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
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
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.unister.semweb.apiontology.data.OntologyUtils;
import com.unister.semweb.apiontology.demonstrator.api.exchange.ConfigurationObject;
import com.unister.semweb.apiontology.demonstrator.api.exchange.ConfigurationObject.Builder;
import com.unister.semweb.apiontology.demonstrator.api.exchange.Constraint;
import com.unister.semweb.apiontology.demonstrator.api.exchange.Equivalence;
import com.unister.semweb.apiontology.demonstrator.api.owl.GD;
import com.unister.semweb.apiontology.util.Constants;

public class ExperimentRunner {

	private static final transient Logger logger = LoggerFactory.getLogger(ExperimentRunner.class);
	@Resource
	private ServiceDiscovery discovery;
	@Resource
	private OntologyUtils dao;
	@Resource
	private OWLOntologyManager manager;

	public ConfigurationObject getConfiguration() {
		Builder builder = ConfigurationObject.builder();
		Set<IRI> parameters = dao.parameters();
		Multimap<String, String> multimap = HashMultimap.create();
		Map<String, String> prefixes = dao.getPrefixes();
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
		builder.datamodel(manager.getOntology(Constants.ADM_ONTOLOGY), dao.getPrefixes());
		return builder.build();
	}

	private String shortIri(IRI iri) {
		if (iri.getNamespace() != null) {
			if (dao.getPrefixes().containsKey(iri.getNamespace())) {
				return dao.getPrefixes().get(iri.getNamespace()) + ":" + iri.getShortForm();
			}
		}
		return iri.toString();
	}

	public OWLOntology addWebService(String service) {
		discovery.discover(service);
		return manager.getOntology(Constants.ADM_ONTOLOGY);
	}

	public OWLOntology addMandatoryParam(String webservice, Collection<String> params) {
		dao.addConstraints(webservice, params);
		return manager.getOntology(Constants.ADM_ONTOLOGY);
	}

	public OWLOntology addEquivalence(String p1, String p2) {
		dao.addEquivalent(p1, p2);
		return manager.getOntology(Constants.ADM_ONTOLOGY);
	}

	public void runExperiment(Map<String, String> params) throws OWLOntologyStorageException {
		int run = 0;

		try {
			OWLOntology baseOntology = manager.getOntology(Constants.ADM_ONTOLOGY);
			baseOntology.getOWLOntologyManager().saveOntology(baseOntology, IRI.create(new File("adm")));
			boolean solvable = true;
			while (solvable) {

				OWLOntology ontology = manager.createOntology(IRI.create(GD.NAMESPACE, "experiment-" + "_" + run),
						Sets.newHashSet(baseOntology));
				ontology.getOWLOntologyManager().saveOntology(ontology, IRI.create(new File("testcontsraints")));
				OntologyUtils ontologyUtils = new OntologyUtils(ontology);
				RunningOntology ro = new RunningOntology(ontology);
				ro.init();
				List<IRI> parameters = Lists.newLinkedList();
				for (Entry<String, String> param : params.entrySet()) {
					parameters.add(ro.addParam(IRI.create(param.getKey()), param.getValue()));
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

					if (dao.getNumberOfMandatoryParams(serviceClass.getIRI().toString()) == parameters.size()) {
						Object[] results = ro.invoke(service, serviceClass);
						processResults(results, ontology, serviceClass.getIRI(), ontologyUtils, params);
						solvable = true;
					}
				}
				ontology.getOWLOntologyManager().removeAxioms(ontology, ontology.getAxioms(service));
				baseOntology = ontology;
				run++;

				// ontology.getOWLOntologyManager().saveOntology(ontology,
				// IRI.create(file));
			}
		} catch (OWLOntologyCreationException | MalformedURLException e) {
			logger.error("Error on service discovery", e);
		} catch (InstantiationException e) {
			logger.error("Error on running experiment ", e);
		} catch (IllegalAccessException e) {
			logger.error("Error on running experiment ", e);
		} catch (ClassNotFoundException e) {
			logger.error("Error on running experiment ", e);
		} catch (NoSuchMethodException e) {
			logger.error("Error on running experiment ", e);
		} catch (SecurityException e) {
			logger.error("Error on running experiment ", e);
		} catch (IllegalArgumentException e) {
			logger.error("Error on running experiment ", e);
		} catch (InvocationTargetException e) {
			logger.error("Error on running experiment ", e);
		}
	}

	private void processResults(Object[] results, OWLOntology ontology, IRI serviceClass, OntologyUtils ontologyUtils,
			Map<String, String> params) {
		for (Object result : results) {

			if (result.getClass().equals(String.class)) {
				List<IRI> iris = getOutputParams(ontology, serviceClass, ontologyUtils, XSD.STRING);
				for (IRI iri : iris) {
					params.put(iri.toString(), (String) result);
					logger.info("Result: {}\ntype: {}",result,iri);
				}
			} else {

				StringWriter writer = new StringWriter();
				JAXB.marshal(result, writer);
				List<IRI> iris = getOutputParams(ontology, serviceClass, ontologyUtils, XSD.BASE_64_BINARY);
				for (IRI iri : iris) {
					String resultAsString = writer.toString();
					params.put(iri.toString(), resultAsString);
					logger.info("Result: {}\ntype: {}",resultAsString,iri);
				}
			}
		}
	}

	private List<IRI> getOutputParams(OWLOntology ontology, IRI serviceClass, OntologyUtils ontologyUtils,
			OWLDatatype datatype) {
		OWLDataFactory owlDataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLAnnotationProperty webService = owlDataFactory.getOWLAnnotationProperty(GD.WEB_SERVICE_PROPERTY);
		List<IRI> iris = Lists.newLinkedList();

		OWLClass paramClass = ontologyUtils.getParamByType(datatype);

		for (OWLSubClassOfAxiom params : ontology.getSubClassAxiomsForSuperClass(paramClass)) {
			for (OWLAnnotationAssertionAxiom axiom : ontology
					.getAnnotationAssertionAxioms(params.getSubClass().asOWLClass().getIRI())) {
				if (axiom.getProperty().equals(webService)) {
					if (axiom.getValue().equals(serviceClass)) {
						iris.add(params.getSubClass().asOWLClass().getIRI());
					}
				}
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
			return ontology.containsAxiom(owlDataFactory.getOWLDataPropertyAssertionAxiom(
					owlDataFactory.getOWLDataProperty(GD.IS_INPUT), owlDataFactory.getOWLNamedIndividual(input), true));
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

}
