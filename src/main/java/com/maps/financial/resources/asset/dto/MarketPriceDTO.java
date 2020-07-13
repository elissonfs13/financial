package com.maps.financial.resources.asset.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class MarketPriceDTO {
	
	@JsonProperty("valor")
	private BigDecimal price;
	
	@JsonProperty("data")
	private LocalDate date;

}
