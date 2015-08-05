package com.unister.semweb.apiontology.demonstrator.api.exchange;

import java.util.Map;

import com.google.common.collect.Maps;

public class ExperimentInput {
	private Map<String, String> values;

	public Map<String, String> getValues() {
		return values;
	}

	public void setValues(Map<String, String> values) {
		this.values = values;
	}

	public static Builder builder(){
		return new Builder();
	}

	public static class Builder {
		private Map<String, String> values;

		public Builder value(String key) {
			if (values == null) {
				values = Maps.newHashMap();
			}
			values.put(key, null);
			return this;
		}

		public ExperimentInput build() {
			ExperimentInput input = new ExperimentInput();
			input.setValues(values);
			return input;
		}

	}
}
