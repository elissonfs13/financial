package com.maps.financial.resources.asset.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maps.financial.domain.asset.MovementType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class AssetMovementDTO {
	
	private Long id;
	
	@JsonProperty("quantidade")
	private BigDecimal quantity;
	
	@JsonProperty("valor")
	private BigDecimal value;
	
	@JsonProperty("data")
	private LocalDate date;
	
	@JsonProperty("tipo")
	private MovementType type;
	
	private String ativo;

}
