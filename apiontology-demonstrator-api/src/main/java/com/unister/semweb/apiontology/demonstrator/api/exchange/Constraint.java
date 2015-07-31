package com.unister.semweb.apiontology.demonstrator.api.exchange;

import java.util.List;
import java.util.Set;

public class Constraint {

	private String webService;
	private Set<String> parameters;

	public String getWebService() {
		return webService;
	}
	public void setWebService(String webService) {
		this.webService = webService;
	}
	public Set<String> getParameters() {
		return parameters;
	}
	public void setParameters(Set<String> parameters) {
		this.parameters = parameters;
	}
}
