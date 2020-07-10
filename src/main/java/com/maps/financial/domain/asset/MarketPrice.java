package com.maps.financial.domain.asset;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 
 * Classe para Valor de Mercado
 * 
 * @author Elisson
 *
 */
@Entity
@Table(name = "market_price")
@Getter
@AllArgsConstructor @NoArgsConstructor @Builder
public class MarketPrice {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@JoinColumn(name = "asset_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Asset asset;
	
	@Basic(optional = false)
	private BigDecimal price;
	
	@Basic(optional = false)
	@Temporal(TemporalType.DATE)
	private LocalDate date;
	
}
