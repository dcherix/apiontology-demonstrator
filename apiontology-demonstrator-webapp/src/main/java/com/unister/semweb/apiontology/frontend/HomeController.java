package com.unister.semweb.apiontology.frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.unister.semweb.apiontology.demonstrator.api.Mock;
import com.unister.semweb.apiontology.demonstrator.api.exchange.ConfigurationsObject;

@Controller
public class HomeController {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {

		ModelAndView model = new ModelAndView("home");

		StringBuilder builder = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(HomeController.class.getClassLoader().getResourceAsStream("testms")))) {
			while (reader.ready()) {
				builder.append(reader.readLine()).append("\n");
			}
		} catch (IOException e) {

		}

		model.addObject("ontology", StringEscapeUtils.escapeHtml4(builder.toString()));
		return model;
	}

	@RequestMapping(value = "/configurations.json", method = RequestMethod.GET)
	@ResponseBody
	public ConfigurationsObject config() {
		return Mock.mock();
	}

	@RequestMapping(value = "/configurations.json", method = RequestMethod.POST)
	@ResponseBody
	public ConfigurationsObject config(@RequestBody ConfigurationsObject object) {
		return Mock.mock();
	}
}
