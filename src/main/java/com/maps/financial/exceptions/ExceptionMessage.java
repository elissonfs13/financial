package com.maps.financial.exceptions;

/**
 * Enum com as mensagens de erros que podem ser lançadas na aplicação
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
public enum ExceptionMessage {
	
	MESSAGE_OBJECT_NOT_FOUND("exception.message.object-not-found"),
	MESSAGE_ASSET_QUANTITY_NOT_AVAILABLE("exception.message.asset.quantity-not-available"),
	MESSAGE_ACCOUNT_BALANACE_NOT_AVAILABLE("exception.message.account.balance-not-available"), 
	MESSAGE_MOVEMENT_NOT_ALLOWED_IN_DATE("exception.message.movement-not-allowed-in-date"),
	MESSAGE_MOVEMENT_NOT_ALLOWED_IN_WEEKEND("exception.message.movement-not-allowed-in-weekend"), 
	MESSAGE_ISSUE_NOT_BEFORE_DUE("exception.message.issue-not-before-due"), 
	MESSAGE_ACCESS_DENIED("exception.message.access-denied"),
	MESSAGE_AUTHENTICATION_FAILED("exception.message.authentication-failed");
	
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
