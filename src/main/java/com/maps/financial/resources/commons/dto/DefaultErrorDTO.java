package com.maps.financial.resources.commons.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class DefaultErrorDTO {
	
	private HttpStatus status;
	private String error;
	private String message;

}
