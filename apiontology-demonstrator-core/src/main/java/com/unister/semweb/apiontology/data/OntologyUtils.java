package com.unister.semweb.apiontology.data;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.io.ReaderDocumentSource;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clarkparsia.owlapiv3.OWL;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.unister.semweb.apiontology.demonstrator.api.owl.GD;
import com.unister.semweb.apiontology.exception.OntologyException;

import uk.ac.manchester.cs.owl.owlapi.turtle.parser.TurtleOntologyParser;

/**
 * Represents a web service
 *
 * @author d.cherix
 *
 */
public class OntologyUtils {

	private static final transient Logger logger = LoggerFactory.getLogger(OntologyUtils.class);

	private static final String SAVE_DIR = null;
	public static void main(String[] args)
			throws OWLOntologyStorageException, OWLOntologyCreationException, OWLParserException, IOException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		String sds = "@prefix : <" + GD.NAMESPACE + "> . " + "@prefix owl: <http://www.w3.org/2002/07/owl#> . \n"
				+ "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n"
				+ "@prefix xml: <http://www.w3.org/XML/1998/namespace> . \n"
				+ "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> . \n"
				+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> . \n"
				+ "@base <http://www.semanticweb.org/d.cherix/ontologies/2015/4/untitled-ontology-179> . \n"
				+ "<http://www.semanticweb.org/d.cherix/ontologies/2015/4/untitled-ontology-179> rdf:type owl:Ontology . \n"
				+ "<http://example.org/resource/WebServiceX> a owl:Class; \n" + "rdfs:subClassOf :WebService; \n"
				+ "owl:equivalentClass [ a owl:Restriction; owl:onProperty :hasParam ; \n"
				+ " owl:someValuesFrom :Person" + "] . \n";
		OWLOntology o = manager.createOntology();
		new TurtleOntologyParser().parse(new ReaderDocumentSource(new StringReader(sds)), o);
		manager.saveOntology(o, new ManchesterOWLSyntaxOntologyFormat(), System.out);
	}
	private OWLDataFactory factory;
	@Resource
	private OWLOntologyManager manager;
	private Map<String, Integer> mandatoryParams;
	@Resource
	private OWLOntology ontology;
	private OWLClass paramClass;

	private BiMap<String, String> prefixes;


	private OWLClass webServiceClass;

	public OntologyUtils() {
	}

	public OntologyUtils(OWLOntology ontology) {
		super();
		this.ontology = ontology;
		this.manager = ontology.getOWLOntologyManager();
		this.init();
	}

	public void addConstraint(String constraint) throws OntologyException {
		OWLOntology o;
		try {
			o = manager.createOntology();
			new TurtleOntologyParser().parse(new ReaderDocumentSource(new StringReader(constraint)), o);
		} catch (OWLOntologyCreationException | IOException e) {
			throw new OntologyException("Unable to add constraint: " + constraint, e);
		}

	}

	public void addConstraints(String webService, Collection<String> params) {
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLObjectProperty hasParam = factory.getOWLObjectProperty(GD.HAS_PARAMETER);

		OWLClass service = factory.getOWLClass(IRI.create(webService));
		Set<OWLClassExpression> expressions = Sets.newHashSet();
		for (String param : params) {
			OWLObjectSomeValuesFrom someValues = factory.getOWLObjectSomeValuesFrom(hasParam,
					factory.getOWLClass(IRI.create(param)));
			expressions.add(someValues);
		}

		OWLObjectExactCardinality cardinality = factory.getOWLObjectExactCardinality(params.size(), hasParam,
				factory.getOWLClass(GD.PARAMETER));
		// expressions.add(cardinality);
		mandatoryParams.put(webService, params.size());
		OWLObjectIntersectionOf intersection = factory.getOWLObjectIntersectionOf(expressions);
		ontology.getOWLOntologyManager().addAxiom(ontology,
				factory.getOWLEquivalentClassesAxiom(service, intersection));
		// ontology.getOWLOntologyManager().addAxiom(ontology,
		// factory.getOWLEquivalentClassesAxiom(service, cardinality));
		// ontology.getOWLOntologyManager().addAxiom(ontology,
		// factory.getOWLSubClassOfAxiom(service, cardinality));

	}

	public void addConstraints(WebService webService) {
		IRI webservice = IRI.create(webService.getUrl());
		webServiceClass = factory.getOWLClass(webservice);
		Set<OWLClassExpression> someValuesFromRestrictions = Sets.newHashSet();
		OWLObjectProperty hasParameterProperty = factory.getOWLObjectProperty(GD.HAS_PARAMETER);
		for (String mandatory : webService.getMandatoryParameters()) {
			someValuesFromRestrictions.add(factory.getOWLObjectSomeValuesFrom(hasParameterProperty,
					factory.getOWLClass(IRI.create(mandatory))));
		}
		OWLObjectUnionOf union = factory.getOWLObjectUnionOf(someValuesFromRestrictions);
		OWLObjectExactCardinality cardinality = factory
				.getOWLObjectExactCardinality(webService.getMandatoryParameters().size(), hasParameterProperty);
		OWLObjectIntersectionOf equivalence = factory.getOWLObjectIntersectionOf(union, cardinality);
		OWLEquivalentClassesAxiom axiom = factory.getOWLEquivalentClassesAxiom(webServiceClass, equivalence);
		manager.addAxiom(ontology, axiom);
	}

	public void addEquivalent(IRI owlClass, OWLClassExpression equivalence) {
		this.addEquivalent(factory.getOWLClass(owlClass), equivalence);
	}

	public void addEquivalent(String p1, String p2){
		this.addEquivalent(IRI.create(p1), IRI.create(p2));
	}

	public void addEquivalent(IRI p1, IRI p2){
		this.addEquivalent(factory.getOWLClass(p1),factory.getOWLClass(p2));
	}

	public void addEquivalent(OWLClass owlClass, OWLClassExpression equivalence) {
		OWLEquivalentClassesAxiom axiom = factory.getOWLEquivalentClassesAxiom(owlClass, equivalence);
		manager.addAxiom(ontology, axiom);
	}

	public void addEquivalent(String owlClass, OWLClassExpression equivalence) {
		this.addEquivalent(factory.getOWLClass(IRI.create(owlClass)), equivalence);
	}

	public void addParam(IRI param, OWLDatatype type, IRI service, boolean input, Class<?> containerClass) {
		OWLClass newParam = factory.getOWLClass(param);

		OWLAxiom axiom;
		OWLClass paramByType = getParamByType(type);
		if (paramByType != null) {
			axiom = factory.getOWLSubClassOfAxiom(newParam, paramByType);
		} else {
			axiom = factory.getOWLSubClassOfAxiom(newParam, createSuperType(type));
		}
		OWLAnnotation annotation = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(GD.WEB_SERVICE_PROPERTY),
				service);

		manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(newParam.getIRI(), annotation));

		OWLAnnotation annotation2 = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(GD.IS_INPUT),
				factory.getOWLLiteral(input));

		manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(newParam.getIRI(), annotation2));
		manager.addAxiom(ontology, axiom);
		manager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(newParam, factory.getOWLClass(GD.PARAMETER)));
		OWLAnnotation containerClassAnnotation;
		if (input) {
			containerClassAnnotation = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(GD.INPUT_NAME),
					factory.getOWLLiteral(containerClass.getName()));

		} else {
			containerClassAnnotation = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(GD.OUTPUT_NAME),
					factory.getOWLLiteral(containerClass.getName()));
		}

		manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(service, containerClassAnnotation));

	}

	public void addPrefix(IRI paramIri) {
		if (paramIri.getNamespace() != null) {
			if (!prefixes.containsKey(paramIri.getNamespace())) {
				prefixes.put(paramIri.getNamespace(), "n" + prefixes.size() + 1);
			}
		}
	}

	public void addServiceParams(String owlClass, List<String> params) {
		OWLClass service = factory.getOWLClass(IRI.create(owlClass));
		OWLObjectProperty hasParam = factory.getOWLObjectProperty(GD.HAS_PARAMETER);
		for (String param : params) {
			this.addEquivalent(service,
					factory.getOWLObjectSomeValuesFrom(hasParam, factory.getOWLClass(IRI.create(param))));
		}
	}

	public void addWebService(IRI name, String wsdlUrl, String operationName) {
		OWLClass service = factory.getOWLClass(name);
		OWLSubClassOfAxiom axiom = factory.getOWLSubClassOfAxiom(service, webServiceClass);
		manager.addAxiom(ontology, axiom);
		OWLAnnotation wsdlAnnotation = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(GD.WSDL_URL),
				factory.getOWLLiteral(wsdlUrl));

		manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(service.getIRI(), wsdlAnnotation));

		OWLAnnotation opNameAnnotation = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(GD.OPERATION_NAME),
				factory.getOWLLiteral(operationName));

		manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(service.getIRI(), opNameAnnotation));
	}

	public void closeWorld() {
		OWLEquivalentClassesAxiom closedAxiom = factory.getOWLEquivalentClassesAxiom(OWL.Thing,
				factory.getOWLObjectOneOf(ontology.getIndividualsInSignature()));
		manager.addAxiom(ontology, closedAxiom);
		manager.addAxiom(ontology, factory.getOWLDifferentIndividualsAxiom(ontology.getIndividualsInSignature()));
	}

	private OWLClass createSuperType(OWLDatatype type) {
		OWLClass superClass = factory.getOWLClass(IRI.create(GD.NAMESPACE + "Param/" + type.getIRI().getShortForm()));
		manager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(superClass, paramClass));
		manager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(superClass,
				factory.getOWLDataSomeValuesFrom(factory.getOWLDataProperty(GD.VALUE), type)));
		return superClass;
	}

	public int getNumberOfMandatoryParams(String webService) {
		return mandatoryParams.get(webService);
	}

	public OWLClass getParamByType(OWLDatatype type) {
		for (OWLClassExpression subClass : paramClass.getSubClasses(ontology)) {
			for (OWLClassExpression superClass : subClass.asOWLClass().getSuperClasses(ontology)) {
				if (superClass.getClassExpressionType().equals(ClassExpressionType.DATA_SOME_VALUES_FROM)) {
					for (OWLDatatype datatype : ((OWLDataSomeValuesFrom) superClass).getFiller()
							.getDatatypesInSignature()) {
						if (datatype.equals(type)) {
							return subClass.asOWLClass();
						}
					}
				}
			}
		}
		return null;
	}

	public BiMap<String, String> getPrefixes() {
		return prefixes;
	}

	@PostConstruct
	public void init() {
		this.factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		this.manager = ontology.getOWLOntologyManager();
		this.webServiceClass = factory.getOWLClass(GD.WEB_SERVICE);
		this.paramClass = factory.getOWLClass(GD.PARAMETER);
		mandatoryParams = Maps.newHashMap();
		prefixes = HashBiMap.create();
		prefixes.put( GD.NAMESPACE,"gd");
		prefixes.put( GD.NAMESPACE+"Param/","gdp");
	}

	public List<OWLIndividual> listAllWebservices() {

		Set<OWLClassAssertionAxiom> assertionsAxioms = ontology.getClassAssertionAxioms(webServiceClass);
		LinkedList<OWLIndividual> individuals = Lists.newLinkedList();
		for (OWLClassAssertionAxiom axiom : assertionsAxioms) {
			individuals.add(axiom.getIndividual());
		}
		return individuals;
	}

	public Set<IRI> parameters() {
		return subClass(factory.getOWLClass(GD.PARAMETER));
	}

	public void save() {
		try {
			manager.saveOntology(ontology, new TurtleOntologyFormat(),
					IRI.create(new File(SAVE_DIR + "ontology" + Calendar.getInstance().getTimeInMillis() + ".ttl")));
		} catch (OWLOntologyStorageException e) {
			logger.error("Error on saving", e);
		}
	}

	public Set<IRI> subClass(OWLClass superClass) {
		Set<IRI> parameters = Sets.newHashSet();
		for (OWLSubClassOfAxiom parameter : ontology.getSubClassAxiomsForSuperClass(superClass)) {
			if (!parameter.getSubClass().isAnonymous()) {
				parameters.add(parameter.getSubClass().asOWLClass().getIRI());
				parameters.addAll(this.subClass(parameter.getSubClass().asOWLClass()));
			}
		}
		return parameters;
	}

	public String webservice(IRI parameter) {
		OWLAnnotationProperty webServiceProperty = factory.getOWLAnnotationProperty(GD.WEB_SERVICE_PROPERTY);
		for (OWLAnnotationAssertionAxiom axiom : ontology.getAnnotationAssertionAxioms(parameter)) {
			if (axiom.getProperty().equals(webServiceProperty)) {
				return axiom.getValue().toString();
			}
		}
		return null;
	}
}
