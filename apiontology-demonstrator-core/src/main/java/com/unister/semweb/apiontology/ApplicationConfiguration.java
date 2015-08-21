package com.unister.semweb.apiontology;

import java.util.Map;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.unister.semweb.apiontology.data.OntologyUtils;
import com.unister.semweb.apiontology.demonstrator.api.exchange.ExperimentInput;
import com.unister.semweb.apiontology.util.Constants;

@Configuration
public class ApplicationConfiguration {

	@Bean
	public ServiceDiscovery serviceDiscovery(){
		return new ServiceDiscovery(webServiceDAO());
	}

	@Bean
	public OntologyUtils webServiceDAO(){
		return new OntologyUtils();
	}

	@Bean
	public OWLOntologyManager manager(){
		return OWLManager.createOWLOntologyManager();
	}

	@Bean
	public OWLOntology ontology() throws OWLOntologyCreationException{
		OWLOntologyManager manager = manager();
		return manager.createOntology(Constants.ADM_ONTOLOGY);
	}

	@Bean
	public ExperimentRunner adm(){
		return new ExperimentRunner();
	}

	public static void main(String[] args) throws OWLOntologyStorageException {
		 AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
		 ExperimentRunner adm = context.getBean(ExperimentRunner.class);
		 adm.addWebService("http://wsf.cdyne.com/WeatherWS/Weather.asmx?WSDL");
		 adm.addMandatoryParam("http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP", Lists.newArrayList("http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP#ZIP"));
		 Map<String, String> params = Maps.newHashMap();
		 params.put("http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP#ZIP", "90001");
		 ExperimentInput input = ExperimentInput.builder().value("http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP#ZIP", "90001", false).build();
		 adm.runExperiment(input);
	}
}
