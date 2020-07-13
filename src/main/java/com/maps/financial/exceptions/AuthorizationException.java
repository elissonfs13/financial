package com.maps.financial.exceptions;

/**
 * Exception será lançada quando Usuário logado não possuir permissão requerida para a ação desejada
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
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
