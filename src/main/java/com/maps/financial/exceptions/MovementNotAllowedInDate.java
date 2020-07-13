package com.maps.financial.exceptions;

/**
 * Exception será lançada quando a data de uma operação não estiver entre as datas permitidas para tal
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
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