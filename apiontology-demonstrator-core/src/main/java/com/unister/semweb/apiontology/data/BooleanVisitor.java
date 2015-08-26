package com.unister.semweb.apiontology.data;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationValueVisitor;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

public class BooleanVisitor implements OWLAnnotationValueVisitor {

	private Boolean result = null;

	public Boolean getResult() {
		return result;
	}

	@Override
	public void visit(IRI iri) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLAnonymousIndividual individual) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLLiteral literal) {
		result = literal.parseBoolean();
	}

}