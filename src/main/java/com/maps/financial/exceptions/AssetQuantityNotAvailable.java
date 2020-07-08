package com.maps.financial.exceptions;

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
