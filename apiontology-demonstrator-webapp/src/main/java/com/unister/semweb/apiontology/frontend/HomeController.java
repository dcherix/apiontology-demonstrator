package com.unister.semweb.apiontology.frontend;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.unister.semweb.apiontology.ExperimentRunner;
import com.unister.semweb.apiontology.demonstrator.api.exchange.ConfigurationObject;
import com.unister.semweb.apiontology.demonstrator.api.exchange.Constraint;
import com.unister.semweb.apiontology.demonstrator.api.exchange.Equivalence;
import com.unister.semweb.apiontology.demonstrator.api.exchange.ExperimentInput;

@Controller
public class HomeController {

	@Resource
	private ExperimentRunner runner;

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

//	@PostConstruct
//	public void init(){
//		runner.addWebService("http://wsf.cdyne.com/WeatherWS/Weather.asmx?WSDL");
//	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {

		ModelAndView model = new ModelAndView("home");
		return model;
	}

	@RequestMapping(value = "/configurations.json", method = RequestMethod.GET)
	@ResponseBody
	public ConfigurationObject config() {
		return runner.getConfiguration();
	}

	@RequestMapping(value = "/configurations.json", method = RequestMethod.POST)
	@ResponseBody
	public ConfigurationObject config(@RequestBody ConfigurationObject object) {
		for(Constraint constraint:object.getConstraints()){
			runner.addMandatoryParam(constraint.getWebService(), constraint.getParameters());
		}
		for(Equivalence equivalence:object.getEquivalences()){
			String p1 = equivalence.getParameter();
			for(String p2 :equivalence.getEqParameters()){
				runner.addEquivalence(p1, p2);
			}
		}
		return runner.getConfiguration();
	}

	public ConfigurationObject runExperiment(ExperimentInput input) throws OWLOntologyStorageException{
		runner.runExperiment(input.getValues());
		return runner.getConfiguration();
	}

	public ConfigurationObject addWebService(String input) {
		runner.addWebService(input);
		return runner.getConfiguration();
	}
}
