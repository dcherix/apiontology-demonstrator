package com.unister.semweb.apiontology;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.unister.semweb.apiontology.demonstrator.api.owl.AO;

public class RunningOntology implements AutoCloseable {

	private OWLOntology baseOntology;
	private IRI ontologyFile;
	private PelletReasoner reasoner;
	private OWLDataFactory factory;
	private OWLOntologyManager manager;

	public RunningOntology(IRI ontologyFile) {
		this.ontologyFile = ontologyFile;
	}

	public void init() throws OWLOntologyCreationException {
		manager = OWLManager.createOWLOntologyManager();
		this.baseOntology = manager
				.loadOntologyFromOntologyDocument(ontologyFile);
		reasoner = PelletReasonerFactory.getInstance().createReasoner(
				baseOntology);
		baseOntology.getOWLOntologyManager()
				.addOntologyChangeListener(reasoner);
		factory = this.baseOntology.getOWLOntologyManager().getOWLDataFactory();

	}

	public void addRequest(List<IRI> params) {
		OWLAnonymousIndividual owlAnonymousIndividual = this.factory
				.getOWLAnonymousIndividual();
		OWLClassAssertionAxiom service = this.factory
				.getOWLClassAssertionAxiom(
						this.factory.getOWLClass(AO.WEB_SERVICE),
						owlAnonymousIndividual);

		for (IRI param : params) {
			factory.getOWLObjectPropertyAssertionAxiom(
					factory.getOWLObjectProperty(AO.HAS_PARAMETER),
					owlAnonymousIndividual,
					factory.getOWLNamedIndividual(param));
		}

		manager.addAxiom(baseOntology, service);

		reasoner.flush();

		Set<OWLClass> classes = owlAnonymousIndividual.getClassesInSignature();
		for (OWLClass owlClass : classes) {
			if (!owlClass.getIRI().equals(AO.WEB_SERVICE)) {

			}
		}
	}

	public Object invoke(OWLAnonymousIndividual individual,
			OWLClass serviceClass) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {

		Object container = null;
		for (OWLAnnotation annotation : serviceClass
				.getAnnotations(baseOntology)) {
			if (annotation.getProperty().getIRI().equals(AO.OBJECT_NAME)) {
				Class<?> containerClass = Thread.currentThread()
						.getContextClassLoader()
						.loadClass(annotation.getValue().toString());
				container = containerClass.newInstance();
			}
		}

		OWLLiteral literal;
		for (OWLIndividual value : individual.getObjectPropertyValues(
				factory.getOWLObjectProperty(AO.HAS_PARAMETER), baseOntology)) {
			literal = value
					.getDataPropertyValues(
							factory.getOWLDataProperty(AO.VALUE), baseOntology)
					.iterator().next();
			String methodName="";
			for (OWLLiteral method : value.getDataPropertyValues(
					factory.getOWLDataProperty(AO.METHOD), baseOntology)) {
				methodName = method.getLiteral();
			}

			Method method = container.getClass().getMethod(methodName, String.class);
			method.invoke(container, literal.getLiteral());
		}



		return null;
	}

	@Override
	public void close() throws Exception {
		manager.removeOntology(baseOntology);
	}
}
