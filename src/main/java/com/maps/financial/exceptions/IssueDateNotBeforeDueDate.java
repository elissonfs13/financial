package com.maps.financial.exceptions;

public class IssueDateNotBeforeDueDate extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor para a exception IssueDateNotBeforeDueDate
	 * 
	 * @param exceptionMessage
	 */
	public IssueDateNotBeforeDueDate(ExceptionMessage exceptionMessage) {
		super(exceptionMessage.getValue());
	}

}