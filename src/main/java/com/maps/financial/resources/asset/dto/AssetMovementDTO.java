package com.maps.financial.resources.asset.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

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
	private BigDecimal quantity;
	private BigDecimal value;
	private LocalDate date;
	private MovementType type;

}
