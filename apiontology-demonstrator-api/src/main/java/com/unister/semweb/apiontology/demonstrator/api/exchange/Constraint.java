package com.unister.semweb.apiontology.demonstrator.api.exchange;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

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

	public static Builder builder(){
		return new Builder();
	}

	public static class Builder {
		private String webService;
		private Set<String> parameters;

		public Builder webService(String webService) {
			this.webService = webService;
			return this;
		}

		public Builder parameter(String parameter) {
			if (parameters == null) {
				parameters = Sets.newHashSet();
			}
			parameters.add(parameter);
			return this;
		}

		public Builder parameters(Collection<String> parameters) {
			if (this.parameters == null) {
				this.parameters = Sets.newHashSet();
			}
			this.parameters.addAll(parameters);
			return this;
		}

		public Constraint build(){
			Constraint constraint = new Constraint();
			constraint.setParameters(parameters);
			constraint.setWebService(webService);
			return constraint;
		}
	}
}
