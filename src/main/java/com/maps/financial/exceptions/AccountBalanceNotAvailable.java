package com.maps.financial.exceptions;

/**
 * Exception será lançada quando a conta corrente do usuário logado não possui saldo suficiente para a operação desejada
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
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
