package com.maps.financial.resources.commons;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.maps.financial.exceptions.AccountBalanceNotAvailable;
import com.maps.financial.exceptions.AssetQuantityNotAvailable;
import com.maps.financial.exceptions.AuthorizationException;
import com.maps.financial.exceptions.IssueDateNotBeforeDueDate;
import com.maps.financial.exceptions.MovementNotAllowedInDate;
import com.maps.financial.exceptions.ObjectNotFoundException;
import com.maps.financial.resources.commons.dto.DefaultErrorDTO;


/**
 * Advice para captura de exceções lançadas no sistema
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
@RestControllerAdvice
public class ApplicationControllerAdvice {
	
	@ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultErrorDTO> handleException(Exception ex){
		HttpStatus statusError = HttpStatus.INTERNAL_SERVER_ERROR;
		return ResponseEntity
				.status(statusError)
				.body(getError(statusError, ex.getMessage()));
    }
	
	@ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<DefaultErrorDTO> handleObjectNotFoundException(ObjectNotFoundException ex){
		HttpStatus statusError = HttpStatus.NO_CONTENT;
		return ResponseEntity
				.status(statusError)
				.body(getError(statusError, ex.getMessage()));
    }
	
	@ExceptionHandler(AccountBalanceNotAvailable.class)
    public ResponseEntity<DefaultErrorDTO> handleAccountBalanceNotAvailable(AccountBalanceNotAvailable ex){
		HttpStatus statusError = HttpStatus.NOT_ACCEPTABLE;
		return ResponseEntity
				.status(statusError)
				.body(getError(statusError, ex.getMessage()));
    }
	
	@ExceptionHandler(AssetQuantityNotAvailable.class)
    public ResponseEntity<DefaultErrorDTO> handleAssetQuantityNotAvailable(AssetQuantityNotAvailable ex){
		HttpStatus statusError = HttpStatus.NOT_ACCEPTABLE;
		return ResponseEntity
				.status(statusError)
				.body(getError(statusError, ex.getMessage()));
    }
	
	@ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<DefaultErrorDTO> handleAuthorizationException(AuthorizationException ex) {
		HttpStatus statusError = HttpStatus.FORBIDDEN;
        return ResponseEntity
				.status(statusError)
				.body(getError(statusError, ex.getMessage()));
    }
	
	@ExceptionHandler(IssueDateNotBeforeDueDate.class)
    public ResponseEntity<DefaultErrorDTO> handleIssueDateNotBeforeDueDate(IssueDateNotBeforeDueDate ex){
		HttpStatus statusError = HttpStatus.NOT_ACCEPTABLE;
		return ResponseEntity
				.status(statusError)
				.body(getError(statusError, ex.getMessage()));
    }
	
	@ExceptionHandler(MovementNotAllowedInDate.class)
    public ResponseEntity<DefaultErrorDTO> handleMovementNotAllowedInDate(MovementNotAllowedInDate ex){
		HttpStatus statusError = HttpStatus.NOT_ACCEPTABLE;
		return ResponseEntity
				.status(statusError)
				.body(getError(statusError, ex.getMessage()));
    }
	
	private DefaultErrorDTO getError(HttpStatus status, String message) {
		return DefaultErrorDTO.builder()
				.status(status)
				.error(status.toString())
				.message(message)
				.build();
	}

}
