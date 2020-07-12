package com.maps.financial.resources.asset.dto;

import java.math.BigDecimal;

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
public class PositionDTO {
	
	public String nomeAtivo;
	public AssetType tipoAtivo;
	public BigDecimal quantidadeTotal;
	public BigDecimal valorMercadoTotal;
	public BigDecimal rendimento;
	public BigDecimal lucro;

}
