package com.maps.financial.exceptions;

/**
 * Exception será lançada quando o ativo não possui quantidade suficiente para a operação requerida
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
public class AssetQuantityNotAvailable extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor para a exception AssetQuantityNotAvailable
	 * 
	 * @param exceptionMessage
	 */
	public AssetQuantityNotAvailable(ExceptionMessage exceptionMessage) {
		super(exceptionMessage.getValue());
	}

}
