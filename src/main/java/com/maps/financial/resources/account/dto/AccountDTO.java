package com.maps.financial.resources.account.dto;

import java.math.BigDecimal;
import java.util.List;

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
public class AccountDTO {
	
	private Long id;
	
	@JsonProperty("saldo")
	private BigDecimal balance;
	
	@JsonProperty("lancamentos")
	private List<LaunchDTO> launches;

}
