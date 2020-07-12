package com.maps.financial.exceptions;

public class AuthorizationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor AuthorizationException
	 * 
	 * @param exceptionMessage
	 */
	public AuthorizationException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage.getValue());
	}

}
