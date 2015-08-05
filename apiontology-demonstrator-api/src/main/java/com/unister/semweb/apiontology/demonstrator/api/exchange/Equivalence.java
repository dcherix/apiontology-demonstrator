package com.unister.semweb.apiontology.demonstrator.api.exchange;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

public class Equivalence {
	private String parameter;
	private Set<String> eqParameters;

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public Set<String> getEqParameters() {
		return eqParameters;
	}

	public void setEqParameters(Set<String> eqParameters) {
		this.eqParameters = eqParameters;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String parameter;
		private Set<String> eqParameters;

		public Builder parameter(String parameter) {
			this.parameter = parameter;
			return this;
		}

		public Builder eqParameter(String eqParameter) {
			if (eqParameters == null) {
				eqParameters = Sets.newHashSet();
			}
			eqParameters.add(eqParameter);
			return this;
		}

		public Builder eqParameters(Collection<String> eqParameters) {
			if (eqParameters == null) {
				eqParameters = Sets.newHashSet();
			}
			eqParameters.addAll(eqParameters);
			return this;
		}

		public Equivalence build() {
			Equivalence eq = new Equivalence();
			eq.setEqParameters(eqParameters);
			eq.setParameter(parameter);
			return eq;
		}
	}
}
