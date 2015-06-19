package com.unister.semweb.apiontology.demonstrator.api.owl;

import org.semanticweb.owlapi.model.IRI;

public class AO {

	public static final String NAMESPACE = "http://ontology.unister.de/ontology/api#";

	public static final IRI HAS_PARAMETER;
	public static final IRI OBJECT_NAME;
	public static final IRI PARAMETER;
	public static final IRI VALUE;
	public static final IRI WEB_SERVICE;

	public static final IRI METHOD = null;

	static {
		HAS_PARAMETER = IRI.create(NAMESPACE, "hasParameter");
		OBJECT_NAME = IRI.create(NAMESPACE,"ObjectName");
		PARAMETER = IRI.create(NAMESPACE, "Parameter");
		VALUE = IRI.create(NAMESPACE, "value");
		WEB_SERVICE = IRI.create(NAMESPACE, "WebService");
	}
}
