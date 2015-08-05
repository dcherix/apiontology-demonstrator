package com.unister.semweb.apiontology.data;

import java.util.List;

import com.google.common.collect.Lists;

public class WebService {
	private List<String> mandatoryParameters;
	private List<String> optionalParameters;
	private String url;

	/**
	 * @param url
	 */
	public WebService(String url) {
		this.url = url;
	}

	/**
	 * @param url
	 * @param optionalParameters
	 * @param mandatoryParameters
	 */
	public WebService(String url, List<String> optionalParameters, List<String> mandatoryParameters) {
		this.url = url;
		this.optionalParameters = optionalParameters;
		this.mandatoryParameters = mandatoryParameters;
	}

	public void addMandatoryParameter(String parameter){
		if (this.optionalParameters == null){
			this.mandatoryParameters = Lists.newLinkedList();
		}
		this.mandatoryParameters.add(parameter);
	}
	public void addOptionalParameter(String parameter){
		if (this.optionalParameters == null){
			this.optionalParameters = Lists.newLinkedList();
		}
		this.optionalParameters.add(parameter);
	}
	public List<String> getMandatoryParameters() {
		return mandatoryParameters;
	}
	public List<String> getOptionalParameters() {
		return optionalParameters;
	}
	public String getUrl() {
		return url;
	}
	public void setMandatoryParameters(List<String> mandatoryParameters) {
		this.mandatoryParameters = mandatoryParameters;
	}

	public void setOptionalParameters(List<String> optionalParameters) {
		this.optionalParameters = optionalParameters;
	}

	public void setUrl(String url) {
		this.url = url;
	}


}
