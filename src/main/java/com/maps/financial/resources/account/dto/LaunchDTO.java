package com.maps.financial.resources.account.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maps.financial.domain.account.LaunchType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class LaunchDTO {
	
	@JsonProperty("descricao")
	private String description;
	
	@JsonProperty("valor")
	private BigDecimal value;
	
	@JsonProperty("data")
	private LocalDate date;
	
	@JsonProperty("tipo")
	private LaunchType type;

}
