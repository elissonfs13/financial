package com.maps.financial.resources.asset.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

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
	private AssetDTO asset;
	private BigDecimal quantity;
	private BigDecimal value;
	private LocalDate date;

}
