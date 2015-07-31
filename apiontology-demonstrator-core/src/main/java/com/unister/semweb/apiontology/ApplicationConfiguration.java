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
import com.unister.semweb.apiontology.data.WebServiceDAO;
import com.unister.semweb.apiontology.util.Constants;

@Configuration
public class ApplicationConfiguration {

	@Bean
	public ServiceDiscovery serviceDiscovery(){
		return new ServiceDiscovery(webServiceDAO());
	}

	@Bean
	public WebServiceDAO webServiceDAO(){
		return new WebServiceDAO();
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
	public ApplicationDataModel adm(){
		return new ApplicationDataModel();
	}

	public static void main(String[] args) throws OWLOntologyStorageException {
		 AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
		 ApplicationDataModel adm = context.getBean(ApplicationDataModel.class);
		 adm.addWebService("http://wsf.cdyne.com/WeatherWS/Weather.asmx?WSDL");
		 adm.addMandatoryParam("http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP", Lists.newArrayList("http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP#ZIP"));
		 Map<String, String> params = Maps.newHashMap();
		 params.put("http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP#ZIP", "90001");
		 adm.runExperiment(params);
	}
}
