package com.maps.financial.exceptions;

public enum ExceptionMessage {
	
	MESSAGE_OBJECT_NOT_FOUND("exception.message.object-not-found"),
	MESSAGE_ASSET_QUANTITY_NOT_AVAILABLE("exception.message.asset.quantity-not-available"),
	MESSAGE_ACCOUNT_BALANACE_NOT_AVAILABLE("exception.message.account.balance-not-available");
	
	private String value;
	
	private ExceptionMessage(String value) {
		this.value = value;
	}
	
	/**
	 * Get value Enum
	 * 
	 * @return String
	 */
	public String getValue() {
		return value;
	}
}
