package com.unister.semweb.apiontology.functions;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;

import com.unister.semweb.apiontology.demonstrator.api.owl.GD;

public class AnnotationsFunctions {

	public static Predicate<OWLAnnotationProperty> isJavaClass() {
		return owlAnnotationProperty -> owlAnnotationProperty.getIRI().equals(GD.JAVA_CLASS);
	}

	public static BiPredicate<OWLAnnotationValue, OWLLiteral> hasValue() {
		return (owlAnnotationValue, value) -> owlAnnotationValue.equals(value);
	}

	public static BiPredicate<OWLAnnotationAssertionAxiom, OWLLiteral> isJavaClassWithValue() {
		return (owlAnnotationAssertionAxiom, value) -> isJavaClass().test(owlAnnotationAssertionAxiom.getProperty())
				&& hasValue().test(owlAnnotationAssertionAxiom.getValue(), value);
	}

	public static BiFunction<OWLClassExpression, OWLOntology, Set<OWLAnnotationAssertionAxiom>> getAnnotations() {
		return (owlClassExpression, ontology) -> ontology
				.getAnnotationAssertionAxioms(owlClassExpression.asOWLClass().getIRI());
	}

	public static Predicate<? super OWLClassExpression> containsJavaClassWithValue(String classMethod,
			OWLOntology ontology, OWLDataFactory factory) {
		OWLLiteral literal = factory.getOWLLiteral(classMethod);
		return p -> getAnnotations().apply(p, ontology).stream().anyMatch(t -> isJavaClassWithValue().test(t, literal));
	}

}
