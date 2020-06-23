package com.unister.semweb.apiontology;

import com.unister.semweb.apiontology.demonstrator.api.owl.GD;
import org.coode.owlapi.obo12.parser.OBO12ParserFactory;
import org.semanticweb.owlapi.annotations.HasPriority;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterSyntaxStorerFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.CommonBaseIRIMapper;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.unister.semweb.apiontology.data.OntologyUtils;
import com.unister.semweb.apiontology.util.Constants;
import org.springframework.context.annotation.FilterType;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.concurrent.NonConcurrentOWLOntologyBuilder;

import javax.inject.Inject;

@Configuration
public class ApplicationConfiguration {

	@Bean
	public SoapServiceDiscovery serviceDiscovery() {
		return new SoapServiceDiscovery(webServiceDAO());
	}

	@Bean
	public OntologyUtils webServiceDAO() {
		return new OntologyUtils();
	}

	@Bean
	public OWLOntologyManager manager() {
	    return OWLManager.createOWLOntologyManager();
	}

	@Bean
	public OWLOntologyIRIMapper owlOntologyIRIMapper(){
	    return new CommonBaseIRIMapper(IRI.create(GD.NAMESPACE));
    }

    @Bean
    public OWLParserFactory owlParserFactory(){
	    return new OBO12ParserFactory();
    }

    @Bean
    public OWLOntologyFactory owlOntologyFactory(){
	    return new OWLOntologyFactoryImpl(new NonConcurrentOWLOntologyBuilder());
    }

	@Bean
	public OWLOntology ontology() throws OWLOntologyCreationException {
		OWLOntologyManager manager = manager();
		manager.loadOntologyFromOntologyDocument(new StreamDocumentSource(ApplicationConfiguration.class
				.getClassLoader().getResourceAsStream("GD.owl"), Constants.GD_ONTOLOGY));
		return manager.createOntology(Constants.ADM_ONTOLOGY);
	}

	@Bean
	public ExperimentRunner adm() {
		return new ExperimentRunner(serviceDiscovery(), webServiceDAO(), manager());
	}
}
