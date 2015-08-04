package com.unister.semweb.apiontology;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

public class ConstraintChecker implements OWLClassExpressionVisitor {

	private OWLOntology ontology;
	private boolean satisfied = false;
	private OWLNamedIndividual individual;

	@Override
	public void visit(OWLClass ce) {
		Set<OWLClassExpression> equivalences = ce.getEquivalentClasses(ontology);
		for (OWLClassExpression equivalence : equivalences) {
			equivalence.accept(this);
		}
	}

	@Override
	public void visit(OWLObjectIntersectionOf ce) {
		for (OWLClassExpression conjunction : ce.asConjunctSet()) {
			conjunction.accept(this);
		}
	}

	@Override
	public void visit(OWLObjectUnionOf ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectComplementOf ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectSomeValuesFrom ce) {
		ce.getProperty();
		if(ce.isDataRestriction()){
			ce.getClassExpressionType();
		} else if(ce.isObjectRestriction()){

		}
	}

	@Override
	public void visit(OWLObjectAllValuesFrom ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectHasValue ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectMinCardinality ce) {
		OWLObjectPropertyExpression property = ce.getProperty();
		int count = 0;
		count = count(ce, property, count);
		this.satisfied = count >= ce.getCardinality();
	}

	@Override
	public void visit(OWLObjectExactCardinality ce) {
		OWLObjectPropertyExpression property = ce.getProperty();
		int count = 0;
		count = count(ce, property, count);
		this.satisfied = count == ce.getCardinality();

	}

	private int count(OWLCardinalityRestriction<?,?,?> ce, OWLPropertyExpression<?,?> property, int count) {
		if (ce.isDataRestriction()) {
			for (OWLDataPropertyAssertionAxiom dataproperty : ontology.getDataPropertyAssertionAxioms(individual)) {
				if (dataproperty.equals(property)) {
					count++;
				}
			}
		} else if (ce.isObjectRestriction()) {
			for (OWLObjectPropertyAssertionAxiom objectProperty : ontology
					.getObjectPropertyAssertionAxioms(individual)) {
				if (objectProperty.equals(property)) {
					count++;
				}
			}
		}
		return count;
	}

	@Override
	public void visit(OWLObjectMaxCardinality ce) {
		OWLObjectPropertyExpression property = ce.getProperty();
		int count = 0;
		count = count(ce, property, count);
		this.satisfied = count <= ce.getCardinality();
	}

	@Override
	public void visit(OWLObjectHasSelf ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLObjectOneOf ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLDataSomeValuesFrom ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLDataAllValuesFrom ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLDataHasValue ce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OWLDataMinCardinality ce) {
		OWLDataPropertyExpression property = ce.getProperty();
		int count = 0;
		count = count(ce, property, count);
		this.satisfied = count >= ce.getCardinality();
	}

	@Override
	public void visit(OWLDataExactCardinality ce) {
		OWLDataPropertyExpression property = ce.getProperty();
		int count = 0;
		count = count(ce, property, count);
		this.satisfied = count == ce.getCardinality();
	}

	@Override
	public void visit(OWLDataMaxCardinality ce) {
		OWLDataPropertyExpression property = ce.getProperty();
		int count = 0;
		count = count(ce, property, count);
		this.satisfied = count <= ce.getCardinality();
	}

}
