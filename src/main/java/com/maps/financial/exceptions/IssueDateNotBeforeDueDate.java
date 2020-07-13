package com.maps.financial.exceptions;

/**
 * Exception será lançada ao tentar cadastrar um ativo com data de emissão posterior a data de vencimento.
 * Validação: emissão deve ser sempre anterior ao vencimento
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
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