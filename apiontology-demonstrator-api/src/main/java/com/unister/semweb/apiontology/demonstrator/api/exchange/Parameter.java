package com.unister.semweb.apiontology.demonstrator.api.exchange;

public class Parameter {
	private String name;
	private boolean isInput;
	private boolean isOptional;

	/**
	 * @param name
	 * @param isInput
	 * @param isOptional
	 */
	public Parameter(String name, boolean isInput, boolean isOptional) {
		this.name = name;
		this.isInput = isInput;
		this.isOptional = isOptional;
	}

	public String getName() {
		return name;
	}

	public boolean isInput() {
		return isInput;
	}

	public boolean isOptional() {
		return isOptional;
	}

	public static Builder builder(String name){
		return new Builder(name);
	}

	public static class Builder{
		private String name;
		private boolean isInput = false;
		private boolean isOptional = false;

		public Builder(String name){
			this.name=name;
		}

		public Builder isInput(boolean isInput){
			this.isInput = isInput;
			return this;
		}

		public Builder isOptional(boolean isOptional){
			this.isOptional = isOptional;
			return this;
		}

		public Parameter build(){
			return new Parameter(name, isInput, isOptional);
		}
	}
}
