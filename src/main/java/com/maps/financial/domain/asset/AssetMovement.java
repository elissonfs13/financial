package com.maps.financial.domain.asset;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Calsse de Movimentações de Ativos
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
@Entity
@Table(name = "movement_asset")
@Getter
@AllArgsConstructor @NoArgsConstructor @Builder
public class AssetMovement {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Setter
	@JoinColumn(name = "asset_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Asset asset;
	
	@Setter
	@Enumerated(EnumType.STRING)
	private MovementType type;
	
	@Setter
	private BigDecimal quantity;
	
	@Setter
	private BigDecimal value;
	
	@Setter
	@Basic(optional = false)
	@Column(name = "date", columnDefinition = "DATE")
	private LocalDate date; //data do movimento
	
	/**
	 * Retorna o valor da movimentação no formato definido
	 * 
	 * @return BigDecimal
	 */
	public BigDecimal getValue() {
		return this.value.setScale(2, BigDecimal.ROUND_DOWN);
	}

}
