package com.unister.semweb.apiontology;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SpecificOntologyChangeBroadcastStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.unister.semweb.apiontology.demonstrator.api.owl.GD;

public class RunningOntology implements AutoCloseable {

	private static final transient Logger logger = LoggerFactory.getLogger(RunningOntology.class);

	private OWLOntology ontology;
	private PelletReasoner reasoner;
	private OWLDataFactory factory;
	private OWLOntologyManager manager;

	public RunningOntology(IRI ontologyFile, OWLOntologyManager manager) throws OWLOntologyCreationException {
		this.manager = manager;
		this.ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
	}

	public RunningOntology(OWLOntology ontology) {
		this.ontology = ontology;
		this.manager = ontology.getOWLOntologyManager();
	}

	public void init() throws OWLOntologyCreationException, MalformedURLException {

		// create the Pellet reasoner
		reasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ontology);

		// add the reasoner as an ontology change listener
		manager.addOntologyChangeListener(reasoner);

		factory = this.ontology.getOWLOntologyManager().getOWLDataFactory();

	}

	public IRI addParam(IRI paramType, String value) {
		OWLNamedIndividual individual = createParam(paramType);
		manager.addAxiom(ontology,
				factory.getOWLDataPropertyAssertionAxiom(factory.getOWLDataProperty(GD.VALUE), individual, value));
		return individual.getIRI();
	}

	public IRI addParam(IRI paramType, double value) {
		OWLNamedIndividual individual = createParam(paramType);
		manager.addAxiom(ontology,
				factory.getOWLDataPropertyAssertionAxiom(factory.getOWLDataProperty(GD.VALUE), individual, value));
		return individual.getIRI();
	}

	public IRI addParam(IRI paramType, long value) {
		OWLNamedIndividual individual = createParam(paramType);
		manager.addAxiom(ontology,
				factory.getOWLDataPropertyAssertionAxiom(factory.getOWLDataProperty(GD.VALUE), individual, value));
		return individual.getIRI();
	}

	private OWLNamedIndividual createParam(IRI paramType) {
		OWLNamedIndividual individual = factory
				.getOWLNamedIndividual(IRI.create(GD.NAMESPACE + "/param/" + UUID.randomUUID()));
		manager.addAxiom(ontology, factory.getOWLClassAssertionAxiom(factory.getOWLClass(paramType), individual));
		return individual;
	}

	public OWLNamedIndividual addRequest(List<IRI> params) {
		OWLNamedIndividual individual = this.factory
				.getOWLNamedIndividual(IRI.create(GD.NAMESPACE + "/service/", UUID.randomUUID().toString()));
		OWLClassAssertionAxiom service = this.factory
				.getOWLClassAssertionAxiom(this.factory.getOWLClass(GD.WEB_SERVICE), individual);

		for (IRI param : params) {
			manager.addAxiom(ontology, factory.getOWLObjectPropertyAssertionAxiom(
					factory.getOWLObjectProperty(GD.HAS_PARAMETER), individual, factory.getOWLNamedIndividual(param)));
		}

		manager.addAxiom(ontology, service);
		return individual;

	}

	public Set<OWLClass> getClasses(OWLNamedIndividual individual) {
		reasoner.flush();
		return reasoner.getTypes(individual, true).getFlattened();
	}

	public Object[] invoke(OWLNamedIndividual individual, OWLClass serviceClass)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {

		String wsdlUrl = "";

		for (OWLAnnotation wsdlAnnotation : serviceClass.getAnnotations(ontology,
				factory.getOWLAnnotationProperty(GD.WSDL_URL))) {
			wsdlUrl = ((OWLLiteral) wsdlAnnotation.getValue()).getLiteral();
			logger.info("Founds wsdl-url {} for service {}", wsdlUrl, serviceClass.getIRI().getShortForm());
		}

		Client client = ClientFactory.getInstance().getClient(wsdlUrl);

		Object container = null;
		for (OWLAnnotation annotation : serviceClass.getAnnotations(ontology,
				factory.getOWLAnnotationProperty(GD.INPUT_NAME))) {
			OWLAnnotationValue annotationValue = annotation.getValue();
			if (annotationValue instanceof OWLLiteral) {
				String className = ((OWLLiteral) annotationValue).getLiteral();
				Class<?> containerClass = Thread.currentThread().getContextClassLoader().loadClass(className);
				container = containerClass.newInstance();
			}
		}

		OWLLiteral literal;
		for (OWLIndividual value : individual.getObjectPropertyValues(factory.getOWLObjectProperty(GD.HAS_PARAMETER),
				ontology)) {
			literal = value.getDataPropertyValues(factory.getOWLDataProperty(GD.VALUE), ontology).iterator().next();
			String methodName = "";

			for (OWLClass paramClass : reasoner.getTypes(value.asOWLNamedIndividual(), true).getFlattened()) {
				methodName = "set" + StringUtils.substringAfterLast(paramClass.getIRI().toString(), "#");
				Method method = container.getClass().getMethod(methodName, String.class);
				if (method != null) {
					method.invoke(container, literal.getLiteral());
				}
			}
			logger.debug("Container class {}", container.getClass());
		}

		try {
			String operationName = "";
			for (OWLAnnotation annotation : serviceClass.getAnnotations(ontology,
					factory.getOWLAnnotationProperty(GD.OPERATION_NAME))) {
				operationName = ((OWLLiteral) annotation.getValue()).getLiteral();
			}
			Object[] results = client.invokeWrapped(operationName, container);
			return results;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void close() throws Exception {
		manager.removeOntology(ontology);
	}

	public static void main(String[] args) throws OWLOntologyCreationException, MalformedURLException,
			InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {

//		RunningOntology ro = new RunningOntology(IRI.create(new File("test")));
//		ro.init();
//		IRI p = ro.addParam(IRI.create("http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP#ZIP"), "90001");
//		OWLNamedIndividual service = ro.addRequest(Lists.newArrayList(p));
//		Set<OWLClass> classes = ro.getClasses(service);
//		System.out.println(classes);
//		for (Iterator<OWLClass> it = classes.iterator(); it.hasNext();) {
//			ro.invoke(service, it.next());
//
//		}

	}
}
