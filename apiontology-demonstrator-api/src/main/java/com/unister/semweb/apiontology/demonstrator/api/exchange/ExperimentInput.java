package com.unister.semweb.apiontology.demonstrator.api.exchange;

import java.util.Map;

import com.google.common.collect.Maps;

public class ExperimentInput {
	private int executionRuns;

	private Map<String, String> values;

	/**
	 * Used to style outputs in the frontend.
	 */
	private Map<String, Boolean> isNew;

	public Map<String, String> getValues() {
		return values;
	}

	public void setValues(Map<String, String> values) {
		this.values = values;
	}

	public Map<String, Boolean> getIsNew() {
		return isNew;
	}

	public void setIsNew(Map<String, Boolean> isNew) {
		this.isNew = isNew;
	}

	public int getExecutionRuns() {
		return executionRuns;
	}

	public void setExecutionRuns(Integer executionRuns) {
		this.executionRuns = executionRuns;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Map<String, String> values;
		private Map<String, Boolean> isNew;
		private int executionRuns;

		// TODO remove this method
		public Builder value(String key) {
			return value(key, null, false);
		}

		public Builder value(String key, String value, boolean isNew) {
			if (values == null) {
				this.values = Maps.newHashMap();
				this.isNew = Maps.newHashMap();
			}
			this.values.put(key, value);
			this.isNew.put(key, isNew);
			return this;
		}

		public Builder executionRuns(int executionRuns) {
			this.executionRuns = executionRuns;
			return this;
		}

		public ExperimentInput build() {
			ExperimentInput input = new ExperimentInput();
			input.setValues(values);
			input.setIsNew(isNew);
			input.setExecutionRuns(executionRuns);
			return input;
		}

	}
}
