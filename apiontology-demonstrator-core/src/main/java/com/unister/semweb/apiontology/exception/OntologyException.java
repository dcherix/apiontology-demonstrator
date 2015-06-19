package com.unister.semweb.apiontology.exception;

public class OntologyException extends Exception {

	private static final long serialVersionUID = 3391285601323260072L;

	public OntologyException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public OntologyException(String message, Throwable cause) {
		super(message, cause);
	}

	public OntologyException(String message) {
		super(message);
	}

	public OntologyException(Throwable cause) {
		super(cause);
	}

}
