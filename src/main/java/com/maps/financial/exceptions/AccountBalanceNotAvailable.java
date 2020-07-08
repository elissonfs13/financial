package com.maps.financial.exceptions;

public class AccountBalanceNotAvailable extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor para a exception AccountBalanceNotAvailable
	 * 
	 * @param exceptionMessage
	 */
	public AccountBalanceNotAvailable(ExceptionMessage exceptionMessage) {
		super(exceptionMessage.getValue());
	}

}
