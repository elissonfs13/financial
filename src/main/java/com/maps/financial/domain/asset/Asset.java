package com.maps.financial.domain.asset;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.maps.financial.exceptions.AssetQuantityNotAvailable;
import com.maps.financial.exceptions.ExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * Classe de Ativos Financeiros
 * 
 * @author Elisson
 *
 */
@Entity
@Table(name = "financial_asset")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @NoArgsConstructor @Builder
public class Asset {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@EqualsAndHashCode.Include
    private Long id;
 
	@Setter
	@Basic(optional = false)
    private String name;
    
	@Setter
	@Basic(optional = false)
    @Column(precision=15, scale=8)
    private BigDecimal marketPrice;
 
	@Setter
	@Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private AssetType type;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "asset", orphanRemoval = true)
    @Basic(optional = false)
	private final List<AssetMovement> movements = new ArrayList<>();
    
    /**
     * Método responsável por retornar a quantidade total de compras do ativo
     * 
     * @return BigDecimal
     */
    private BigDecimal getQuantityBuy() {
    	return this.getMovements()
    			.stream()
    			.filter(movement -> MovementType.BUY.equals(movement.getType()))
    			.map(movement -> movement.getQuantity())
    			.reduce(BigDecimal.ZERO, BigDecimal::add);			   
    }
    
    /**
     * Método responsável por retornar o valor total de compras do ativo
     * 
     * @return BigDecimal
     */
    private BigDecimal getTotalValueBuy() {
    	return this.getMovements()
    			.stream()
    			.filter(movement -> MovementType.BUY.equals(movement.getType()))
    			.map(movement -> movement.getValue())
    			.reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Método responsável por retornar a quantidade total de vendas do ativo
     * 
     * @return BigDecimal
     */
    private BigDecimal getQuantitySell() {
    	return this.getMovements()
    			.stream()
    			.filter(movement -> MovementType.SELL.equals(movement.getType()))
    			.map(movement -> movement.getQuantity())
    			.reduce(BigDecimal.ZERO, BigDecimal::add);			   
    }
    
    /**
     * Método responsável por retornar o valor total de vendas do ativo
     * 
     * @return BigDecimal
     */
    private BigDecimal getTotalValueSell() {
    	return this.getMovements()
    			.stream()
    			.filter(movement -> MovementType.SELL.equals(movement.getType()))
    			.map(movement -> movement.getValue())
    			.reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Método responsável por calcular e retornar o preço médio das compras
     * 
     * @return BigDecimal
     */
    private BigDecimal getAverageValueBuy() {
    	return this.getTotalValueBuy().divide(this.getQuantityBuy());
    }
    
    /**
     * Método responsável por calcular e retornar a quantidade total atual do ativo
     * 
     * @return BigDecimal
     */
    public BigDecimal getTotalQuantity() {
    	return this.getQuantityBuy().subtract(this.getQuantitySell());
    }
    
    /**
     * Método responsável por calcular e retornar o valor de mercado total atual do ativo
     * 
     * @return BigDecimal
     */
    public BigDecimal getTotalMarketPlace() {
    	return this.getTotalQuantity().multiply(this.getMarketPrice());
    }
    
    /**
     * Método responsável por calcular e retornar o valor do rendimento 
     * 
     * @return BigDecimal
     */
    public BigDecimal getIncome() {
    	return this.getMarketPrice().divide(this.getAverageValueBuy());
    }
    
    /**
     * Método responsável por calcular e retornar o valor do lucro 
     * 
     * @return BigDecimal
     */
    public BigDecimal getProfit() {
    	return this.getTotalValueSell().subtract(this.getTotalValueBuy()); 
    }
    
    /**
     * Método responsável por adicionar uma nova movimentação do ativo
     * Caso de movimentação de venda, ocorre a verificação para a garantia de quantidade disponível do ativo para venda
     * 
     * @param newMovement
     */
    public void includeMovement(AssetMovement newMovement) {
    	if (MovementType.SELL.equals(newMovement.getType()) && newMovement.getQuantity().compareTo(this.getTotalQuantity()) == 1) {
    		throw new AssetQuantityNotAvailable(ExceptionMessage.MESSAGE_ASSET_QUANTITY_NOT_AVAILABLE);
    	}
    	
    	this.movements.add(newMovement);
    }

}
