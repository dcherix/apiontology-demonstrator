package com.unister.semweb.apiontology;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.DefaultOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.google.common.collect.Lists;
import com.unister.semweb.apiontology.demonstrator.api.owl.GD;

public class RunningOntology implements AutoCloseable {

	private OWLOntology baseOntology;
	private IRI ontologyFile;
	private PelletReasoner reasoner;
	private OWLDataFactory factory;
	private OWLOntologyManager manager;

	public RunningOntology(IRI ontologyFile) {
		this.ontologyFile = ontologyFile;
	}

	public void init() throws OWLOntologyCreationException, MalformedURLException {
		manager = OWLManager.createOWLOntologyManager();
		this.baseOntology = manager.loadOntologyFromOntologyDocument(ontologyFile);

		// create the Pellet reasoner
		reasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(baseOntology);

		// add the reasoner as an ontology change listener
		manager.addOntologyChangeListener(reasoner);

		factory = this.baseOntology.getOWLOntologyManager().getOWLDataFactory();

	}

	public IRI addParam(IRI paramType, String value) {
		OWLNamedIndividual individual = createParam(paramType);
		manager.addAxiom(baseOntology,
				factory.getOWLDataPropertyAssertionAxiom(factory.getOWLDataProperty(GD.VALUE), individual, value));
		return individual.getIRI();
	}

	public IRI addParam(IRI paramType, double value) {
		OWLNamedIndividual individual = createParam(paramType);
		manager.addAxiom(baseOntology,
				factory.getOWLDataPropertyAssertionAxiom(factory.getOWLDataProperty(GD.VALUE), individual, value));
		return individual.getIRI();
	}

	public IRI addParam(IRI paramType, long value) {
		OWLNamedIndividual individual = createParam(paramType);
		manager.addAxiom(baseOntology,
				factory.getOWLDataPropertyAssertionAxiom(factory.getOWLDataProperty(GD.VALUE), individual, value));
		return individual.getIRI();
	}

	private OWLNamedIndividual createParam(IRI paramType) {
		OWLNamedIndividual individual = factory
				.getOWLNamedIndividual(IRI.create(GD.NAMESPACE + "/param/" + UUID.randomUUID()));
		manager.addAxiom(baseOntology, factory.getOWLClassAssertionAxiom(factory.getOWLClass(paramType), individual));
		return individual;
	}

	public OWLNamedIndividual addRequest(List<IRI> params) {
		OWLNamedIndividual individual = this.factory
				.getOWLNamedIndividual(IRI.create(GD.NAMESPACE + "/service/", UUID.randomUUID().toString()));
		OWLClassAssertionAxiom service = this.factory
				.getOWLClassAssertionAxiom(this.factory.getOWLClass(GD.WEB_SERVICE), individual);

		for (IRI param : params) {
			manager.addAxiom(baseOntology, factory.getOWLObjectPropertyAssertionAxiom(
					factory.getOWLObjectProperty(GD.HAS_PARAMETER), individual, factory.getOWLNamedIndividual(param)));
		}

		manager.addAxiom(baseOntology, service);
		return individual;

	}

	public Set<OWLClass> getClasses(OWLNamedIndividual individual) {
		return reasoner.getTypes(individual, true).getFlattened();
	}

	public Object invoke(OWLNamedIndividual individual, OWLClass serviceClass)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {

		String wsdlUrl = "";

		for (OWLAnnotation wsdlAnnotation : serviceClass.getAnnotations(baseOntology,
				factory.getOWLAnnotationProperty(GD.WSDL_URL))) {
			wsdlUrl = ((OWLLiteral) wsdlAnnotation.getValue()).getLiteral();
		}
		Client client = ClientFactory.getInstance().getClient(wsdlUrl);

		Object container = null;
		for (OWLAnnotation annotation : serviceClass.getAnnotations(baseOntology,
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
				baseOntology)) {
			literal = value.getDataPropertyValues(factory.getOWLDataProperty(GD.VALUE), baseOntology).iterator().next();
			String methodName = "";

			for (OWLClass paramClass : reasoner.getTypes(value.asOWLNamedIndividual(), true).getFlattened()) {
				methodName = "set" + StringUtils.substringAfterLast(paramClass.getIRI().toString(), "#");
				Method method = container.getClass().getMethod(methodName, String.class);
				if (method != null) {
					method.invoke(container, literal.getLiteral());
				}
			}
			System.out.println(container);
		}

		try {
			String operationName = "";
			for (OWLAnnotation annotation : serviceClass.getAnnotations(baseOntology,
					factory.getOWLAnnotationProperty(GD.OPERATION_NAME))) {
				operationName = ((OWLLiteral) annotation.getValue()).getLiteral();
			}
			Object[] results = client.invokeWrapped(operationName, container);
			for (Object o : results) {
				for (Method method : o.getClass().getMethods()) {
					if (method.getName().startsWith("get")) {
						System.out.println(method.getName() + " " + method.invoke(o).toString());
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void close() throws Exception {
		manager.removeOntology(baseOntology);
	}

	public static void main(String[] args) throws OWLOntologyCreationException, MalformedURLException,
			InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {

		RunningOntology ro = new RunningOntology(IRI.create(new File("test")));
		ro.init();
		IRI p = ro.addParam(IRI.create("http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP#ZIP"), "90001");
		OWLNamedIndividual service = ro.addRequest(Lists.newArrayList(p));
		Set<OWLClass> classes = ro.getClasses(service);
		System.out.println(classes);
		for (Iterator<OWLClass> it = classes.iterator(); it.hasNext();) {
			ro.invoke(service, it.next());

		}

	}
}
