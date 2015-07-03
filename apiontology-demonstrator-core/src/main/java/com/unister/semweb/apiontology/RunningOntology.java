package com.unister.semweb.apiontology;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.DefaultOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
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
import org.semanticweb.owlapi.owllink.OWLlinkHTTPXMLReasonerFactory;
import org.semanticweb.owlapi.owllink.OWLlinkReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import com.google.common.collect.Lists;
import com.unister.semweb.apiontology.demonstrator.api.owl.GD;

public class RunningOntology implements AutoCloseable {

	private OWLOntology baseOntology;
	private IRI ontologyFile;
	private OWLReasoner reasoner;
	private OWLDataFactory factory;
	private OWLOntologyManager manager;

	public RunningOntology(IRI ontologyFile) {
		this.ontologyFile = ontologyFile;
	}

	public void init() throws OWLOntologyCreationException, MalformedURLException {
		manager = OWLManager.createOWLOntologyManager();
		this.baseOntology = manager
				.loadOntologyFromOntologyDocument(ontologyFile);

		URL url = new URL("http://localhost:8080");
		OWLlinkReasonerConfiguration reasonerConfiguration =
			new OWLlinkReasonerConfiguration(url);

		OWLlinkHTTPXMLReasonerFactory reasonerFactory = new OWLlinkHTTPXMLReasonerFactory();
	 	reasoner =
			reasonerFactory.createReasoner(baseOntology, reasonerConfiguration);

		factory = this.baseOntology.getOWLOntologyManager().getOWLDataFactory();

	}

	public IRI addParam(IRI paramType, String value) {
		OWLNamedIndividual individual = createParam(paramType);
		manager.addAxiom(
				baseOntology,
				factory.getOWLDataPropertyAssertionAxiom(
						factory.getOWLDataProperty(GD.VALUE), individual, value));
		return individual.getIRI();
	}

	public IRI addParam(IRI paramType, double value) {
		OWLNamedIndividual individual = createParam(paramType);
		manager.addAxiom(
				baseOntology,
				factory.getOWLDataPropertyAssertionAxiom(
						factory.getOWLDataProperty(GD.VALUE), individual, value));
		return individual.getIRI();
	}

	public IRI addParam(IRI paramType, long value) {
		OWLNamedIndividual individual = createParam(paramType);
		manager.addAxiom(
				baseOntology,
				factory.getOWLDataPropertyAssertionAxiom(
						factory.getOWLDataProperty(GD.VALUE), individual, value));
		return individual.getIRI();
	}

	private OWLNamedIndividual createParam(IRI paramType) {
		OWLNamedIndividual individual = factory.getOWLNamedIndividual(IRI
				.create(GD.NAMESPACE + "/param/" + UUID.randomUUID()));
		manager.addAxiom(
				baseOntology,
				factory.getOWLClassAssertionAxiom(
						factory.getOWLClass(paramType), individual));
		return individual;
	}

	public void addRequest(List<IRI> params) {
		OWLNamedIndividual individual = this.factory.getOWLNamedIndividual(IRI
				.create(GD.NAMESPACE + "/service/", UUID.randomUUID()
						.toString()));
		OWLClassAssertionAxiom service = this.factory
				.getOWLClassAssertionAxiom(
						this.factory.getOWLClass(GD.WEB_SERVICE), individual);

		for (IRI param : params) {
			manager.addAxiom(baseOntology, factory
					.getOWLObjectPropertyAssertionAxiom(
							factory.getOWLObjectProperty(GD.HAS_PARAMETER),
							individual, factory.getOWLNamedIndividual(param)));
		}

		manager.addAxiom(baseOntology, service);

		NodeSet<OWLClass> types = reasoner.getTypes(individual, false);
		System.out.println(types);

		System.out.println(reasoner.getPrecomputableInferenceTypes());
		reasoner.flush();

		types = reasoner.getTypes(individual, false);
		System.out.println(types);

		Set<OWLClass> classes = individual.getClassesInSignature();
		for (OWLClass owlClass : classes) {
			if (!owlClass.getIRI().equals(GD.WEB_SERVICE)) {
				System.out.println(owlClass);
			}
		}

		try {
			manager.saveOntology(baseOntology, new DefaultOntologyFormat(), new FileOutputStream("result.rdf"));
		} catch (OWLOntologyStorageException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Object invoke(OWLAnonymousIndividual individual,
			OWLClass serviceClass) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			NoSuchMethodException, SecurityException, IllegalArgumentException,
			InvocationTargetException {

		Object container = null;
		for (OWLAnnotation annotation : serviceClass
				.getAnnotations(baseOntology)) {
			if (annotation.getProperty().getIRI().equals(GD.OBJECT_NAME)) {
				Class<?> containerClass = Thread.currentThread()
						.getContextClassLoader()
						.loadClass(annotation.getValue().toString());
				container = containerClass.newInstance();
			}
		}

		OWLLiteral literal;
		for (OWLIndividual value : individual.getObjectPropertyValues(
				factory.getOWLObjectProperty(GD.HAS_PARAMETER), baseOntology)) {
			literal = value
					.getDataPropertyValues(
							factory.getOWLDataProperty(GD.VALUE), baseOntology)
					.iterator().next();
			String methodName = "";
			for (OWLLiteral method : value.getDataPropertyValues(
					factory.getOWLDataProperty(GD.METHOD), baseOntology)) {
				methodName = method.getLiteral();
			}

			Method method = container.getClass().getMethod(methodName,
					String.class);
			method.invoke(container, literal.getLiteral());
		}

		return null;
	}

	@Override
	public void close() throws Exception {
		manager.removeOntology(baseOntology);
	}

	public static void main(String[] args) throws OWLOntologyCreationException, MalformedURLException {
		RunningOntology ro = new RunningOntology(IRI.create(new File("test")));
		ro.init();
		IRI p = ro
				.addParam(
						IRI.create("http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP#ZIP"),
						"34567");
		ro.addRequest(Lists.newArrayList(p));
	}
}
