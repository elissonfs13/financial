package com.maps.financial.resources.asset.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
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
	
	@JsonProperty("nome")
    private String name;
	
	@JsonProperty("tipo")
    private AssetType type;
	
	@JsonProperty("dataEmissao")
    private LocalDate issueDate;
	
	@JsonProperty("dataVencimento")
    private LocalDate dueDate;
	
	@JsonProperty("movimentacoes")
    private List<AssetMovementDTO> movements;
	
	@JsonProperty("precosMercado")
    private List<MarketPriceDTO> marketPrices;

}
