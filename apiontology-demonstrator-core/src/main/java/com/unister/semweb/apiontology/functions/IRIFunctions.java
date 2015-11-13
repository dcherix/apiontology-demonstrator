package com.unister.semweb.apiontology.functions;

import java.util.function.Function;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public class IRIFunctions {

	public static Function<IRI, OWLClass> asOWLClass() {
		return iri -> OWLManager.getOWLDataFactory().getOWLClass(iri);
	}

	public static Function<IRI, OWLObjectProperty> asOWLObjectProperty = iri -> OWLManager.getOWLDataFactory()
			.getOWLObjectProperty(iri);

	public static Function<String, IRI> asIri = str -> IRI.create(str);
}
