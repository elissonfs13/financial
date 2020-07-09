package com.maps.financial.exceptions;

public class MovementNotAllowedInDate extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor para a exception MovementNotAllowedInDate
	 * 
	 * @param exceptionMessage
	 */
	public MovementNotAllowedInDate(ExceptionMessage exceptionMessage) {
		super(exceptionMessage.getValue());
	}

}