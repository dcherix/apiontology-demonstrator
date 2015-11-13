package com.unister.semweb.apiontology.functions;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.unister.semweb.apiontology.demonstrator.api.owl.GD;

public class OWLClassFunctions {

	private OWLClassFunctions() {
	}

	public static BiFunction<OWLClass, OWLOntology, Set<OWLSubClassOfAxiom>> subClasses() {
		return (owlClass, ontology) -> ontology.getSubClassAxiomsForSubClass(owlClass);
	}

	public static Function<IRI, OWLClass> getOWLClass = (iri) -> OWLManager.getOWLDataFactory().getOWLClass(iri);
	public static Function<String, OWLClass> getOWLClassFromString = str -> getOWLClass.apply(IRI.create(str));

	public static BiFunction<OWLObjectProperty, OWLClassExpression, OWLObjectSomeValuesFrom> getOWLObjectSomeValuesFrom(
			OWLDataFactory factory) {
		return (property, classExpression) -> factory.getOWLObjectSomeValuesFrom(property, classExpression);
	}

	public static BiFunction<OWLObjectProperty, OWLClassExpression, OWLObjectSomeValuesFrom> getOWLObjectSomeValuesFrom = (
			property,
			classExpression) -> OWLManager.getOWLDataFactory().getOWLObjectSomeValuesFrom(property, classExpression);
	public final static Function<String, OWLObjectProperty> getOWLObjectProperty = property -> OWLManager
			.getOWLDataFactory().getOWLObjectProperty(IRI.create(property));

	public static BiFunction<String, String, OWLObjectSomeValuesFrom> getOWLObjectSomeValuesFromString = (property,
			classExpression) -> getOWLObjectSomeValuesFrom.apply(getOWLObjectProperty.apply(property),
					OWLManager.getOWLDataFactory().getOWLClass(IRI.create(classExpression)));

	public static Function<OWLClassExpression, OWLObjectSomeValuesFrom> hasParameterOWLObjectSomeValues = classExpression -> getOWLObjectSomeValuesFrom
			.apply(IRIFunctions.asOWLObjectProperty.apply(GD.HAS_PARAMETER), classExpression);
}
