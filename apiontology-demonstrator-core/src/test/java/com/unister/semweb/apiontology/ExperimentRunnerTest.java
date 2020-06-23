package com.unister.semweb.apiontology;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Sets;
import com.unister.semweb.apiontology.data.OntologyUtils;
import com.unister.semweb.apiontology.demonstrator.api.exchange.ConfigurationObject;
import com.unister.semweb.apiontology.demonstrator.api.exchange.ExperimentInput;
import com.unister.semweb.apiontology.demonstrator.api.owl.GD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
public class ExperimentRunnerTest {
	@Autowired
	private SoapServiceDiscovery discovery;
    @Autowired
	private OntologyUtils dao;
    @Autowired
	private OWLOntologyManager manager;
    @Autowired
	private OWLOntology ontology;
    @Autowired
	private ExperimentRunner runner;

	@Before
	public void init() {
		addParameters(ontology);
	}

	@Test
	public void testConfiguration() throws OWLOntologyCreationException {

		OWLOntology tmpOnt = generateTmpOntology("http://example.org/testConfiguration");

		ConfigurationObject configObj = runner.getConfiguration(tmpOnt);

		assertThat(configObj, is(not(nullValue())));
		assertThat(configObj.getEquivalences(), is(not(nullValue())));
		assertThat(configObj.getConstraints(), is(nullValue()));
		assertThat(configObj.getStandardDatamodel(), is(not(nullValue())));
		assertThat(configObj.getDatamodel(), is(not(nullValue())));
	}

	@Test
	public void testExperimentInput() throws OWLOntologyCreationException {
		ExperimentInput input = runner.getExperimentInput();
		for (int i = 0; i < 3; i++) {
			assertThat(input.getValues().get("http://example.org/parameter" + i), is(nullValue()));
		}
	}

	private void addParameters(OWLOntology tmpOnt) {
		for (int i = 0; i < 3; i++) {
			OWLDataFactory owlDataFactory = manager.getOWLDataFactory();
			OWLSubClassOfAxiom axiom = owlDataFactory.getOWLSubClassOfAxiom(
					owlDataFactory.getOWLClass(IRI.create("http://example.org/parameter" + i)),
					owlDataFactory.getOWLClass(GD.PARAMETER));
			manager.addAxiom(tmpOnt, axiom);
		}
	}

	private OWLOntology generateTmpOntology(String iri) throws OWLOntologyCreationException {
		return manager.createOntology(IRI.create(iri), Sets.newHashSet(ontology));
	}

}
