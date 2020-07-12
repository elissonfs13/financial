package com.maps.financial.resources.asset.dto;

import java.time.LocalDate;
import java.util.List;

import com.maps.financial.domain.asset.AssetType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class AssetDTO {
	
	private Long id;
    private String name;
    private AssetType type;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private List<AssetMovementDTO> movements;
    private List<MarketPriceDTO> marketPrices;

}
