package com.unister.semweb.apiontology.demonstrator.api.owl;

import org.semanticweb.owlapi.model.IRI;

public class GD {
	
	public static final String NAMESPACE = "http://ontology.unister.de/ontology/api#";

	public static final IRI HAS_PARAMETER;
	public static final IRI IS_INPUT;
	public static final IRI METHOD;
	public static final IRI OBJECT_NAME;
	public static final IRI INPUT_NAME;
	public static final IRI OUTPUT_NAME;
	public static final IRI PARAMETER;
	public static final IRI VALUE;
	public static final IRI WEB_SERVICE;
	public static final IRI WEB_SERVICE_PROPERTY;
	public static final IRI WSDL_URL;
	public static final IRI OPERATION_NAME;

	static {
		HAS_PARAMETER = IRI.create(NAMESPACE, "hasParameter");
		IS_INPUT = IRI.create(NAMESPACE, "isInput");
		INPUT_NAME = IRI.create(NAMESPACE,"inputClass");
		METHOD = IRI.create(NAMESPACE,"method");
		OBJECT_NAME = IRI.create(NAMESPACE, "ObjectName");
		OPERATION_NAME = IRI.create(NAMESPACE,"operationName");
		OUTPUT_NAME = IRI.create(NAMESPACE,"outputClass");
		PARAMETER = IRI.create(NAMESPACE, "Parameter");
		VALUE = IRI.create(NAMESPACE, "value");
		WEB_SERVICE = IRI.create(NAMESPACE, "WebService");
		WEB_SERVICE_PROPERTY = IRI.create(NAMESPACE, "webService");
		WSDL_URL = IRI.create(NAMESPACE,"wsdlUrl");
	}
}
