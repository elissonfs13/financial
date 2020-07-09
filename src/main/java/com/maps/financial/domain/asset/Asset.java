package com.maps.financial.domain.asset;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.maps.financial.exceptions.AssetQuantityNotAvailable;
import com.maps.financial.exceptions.ExceptionMessage;
import com.maps.financial.exceptions.MovementNotAllowedInDate;

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
    @Enumerated(EnumType.STRING)
    private AssetType type;
	
	@Basic(optional = false)
	@Column(name = "issue_date")
	@Temporal(TemporalType.DATE)
	private LocalDate issueDate; //data de emissão
	
	@Basic(optional = false)
	@Column(name = "due_date")
	@Temporal(TemporalType.DATE)
	private LocalDate dueDate; //data de vencimento
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "asset", orphanRemoval = true)
	private final List<AssetMovement> movements = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "asset", orphanRemoval = true)
	private final List<MarketPrice> marketPrices = new ArrayList<>();
    
    /**
     * Método responsável por calcular e retornar a quantidade total atual do ativo
     * 
     * @return BigDecimal
     */
    public BigDecimal getTotalQuantity(LocalDate atualDate) {
    	return this.getQuantityBuy(atualDate).subtract(this.getQuantitySell(atualDate));
    }
    
    /**
     * Método responsável por calcular e retornar o valor de mercado total atual do ativo
     * 
     * @return BigDecimal
     */
    public BigDecimal getTotalMarketPrice(LocalDate atualDate) {
    	BigDecimal totalMarketPrice = this.getTotalQuantity(atualDate).multiply(this.getMarketPrice());
    	return totalMarketPrice.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por calcular e retornar o valor do rendimento 
     * 
     * @return BigDecimal
     */
    public BigDecimal getIncome(LocalDate atualDate) {
    	BigDecimal income = this.getMarketPrice().divide(this.getAverageValueBuy(atualDate), BigDecimal.ROUND_DOWN);
    	return income.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por calcular e retornar o valor do lucro 
     * 
     * @return BigDecimal
     */
    public BigDecimal getProfit(LocalDate atualDate) {
    	BigDecimal profit = this.getTotalValueSell(atualDate).subtract(this.getTotalValueBuy(atualDate)); 
    	return profit.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por adicionar uma nova movimentação do ativo após passar pelas validações
     * 
     * @param newMovement
     */
    public void includeMovement(AssetMovement newMovement) {
    	this.movementValidations(newMovement);
    	this.movements.add(newMovement);
    }
    
    /**
     * Método responsável por adicionar um valor de mercado ao ativo
     * 
     * @param price
     * @param date
     */
    public void includeMarketPrice(BigDecimal price, LocalDate date) {
    	if (price != null && date != null) {
	    	MarketPrice marketPrice = MarketPrice.builder()
	    			.price(price.setScale(8, BigDecimal.ROUND_DOWN))
	    			.date(date)
	    			.build();
	    	
	    	this.marketPrices.add(marketPrice);
    	}
    }
    
    /**
     * Método responsável por excluir os valores de mercado do ativo em uma determinada data 
     * 
     * @param date
     */
    public void excludeMarketPrice(LocalDate date) {
    	List<MarketPrice> marketPricesFind = this.marketPrices
    			.stream()
    			.filter(p -> p.getDate() == date)
    			.collect(Collectors.toList());
    	
    	for (MarketPrice marketPrice : marketPricesFind) {
    		this.marketPrices.remove(marketPrice);
    	}
    }
    
    /**
     * Método responsável por retornar a quantidade total de compras do ativo até a data informada
     * @param atualDate 
     * 
     * @return BigDecimal
     */
    private BigDecimal getQuantityBuy(LocalDate atualDate) {
    	if (atualDate == null) {
    		return BigDecimal.ZERO;
    	}
    	BigDecimal totalQuantity = this.getMovements()
    			.stream()
    			.filter(movement -> MovementType.BUY.equals(movement.getType()) && atualDate.isAfter(movement.getDate()))
    			.map(movement -> movement.getQuantity())
    			.reduce(BigDecimal.ZERO, BigDecimal::add);		
    	
    	return totalQuantity.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por retornar o valor total de compras do ativo até a data informada
     * @param atualDate 
     * 
     * @return BigDecimal
     */
    private BigDecimal getTotalValueBuy(LocalDate atualDate) {
    	if (atualDate == null) {
    		return BigDecimal.ZERO;
    	}
    	BigDecimal totalValue = this.getMovements()
    			.stream()
    			.filter(movement -> MovementType.BUY.equals(movement.getType()) && atualDate.isAfter(movement.getDate()))
    			.map(movement -> movement.getValue())
    			.reduce(BigDecimal.ZERO, BigDecimal::add);
    	
    	return totalValue.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por retornar a quantidade total de vendas do ativo até a data informada
     * @param atualDate 
     * 
     * @return BigDecimal
     */
    private BigDecimal getQuantitySell(LocalDate atualDate) {
    	if (atualDate == null) {
    		return BigDecimal.ZERO;
    	}
    	BigDecimal totalQuantity = this.getMovements()
    			.stream()
    			.filter(movement -> MovementType.SELL.equals(movement.getType()) && atualDate.isAfter(movement.getDate()))
    			.map(movement -> movement.getQuantity())
    			.reduce(BigDecimal.ZERO, BigDecimal::add);		
    	
    	return totalQuantity.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por retornar o valor total de vendas do ativo até a data informada
     * 
     * @return BigDecimal
     */
    private BigDecimal getTotalValueSell(LocalDate atualDate) {
    	if (atualDate == null) {
    		return BigDecimal.ZERO;
    	}
    	BigDecimal totalValue = this.getMovements()
    			.stream()
    			.filter(movement -> MovementType.SELL.equals(movement.getType()) && atualDate.isAfter(movement.getDate()))
    			.map(movement -> movement.getValue())
    			.reduce(BigDecimal.ZERO, BigDecimal::add);
    	
    	return totalValue.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por calcular e retornar o preço médio das compras
     * 
     * @return BigDecimal
     */
    private BigDecimal getAverageValueBuy(LocalDate atualDate) {
    	BigDecimal averageValueBuy = this.getTotalValueBuy(atualDate).divide(this.getQuantityBuy(atualDate), BigDecimal.ROUND_DOWN);
    	return averageValueBuy.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por obter e retornar o último valor de mercado do ativo
     * Retorna ZERO caso não tenha nenhum preço de mercado cadastrado para o ativo
     * 
     * @return BigDecimal
     */
    private BigDecimal getMarketPrice() {
    	Comparator<MarketPrice> comparator = Comparator.comparing(MarketPrice::getDate);
    	MarketPrice marketPrice = this.marketPrices.stream().max(comparator).orElse(null);
    	return marketPrice != null ? marketPrice.getPrice() : BigDecimal.ZERO;
    }
    
    /**
     * Método responsável pela realização das devidas validações para a inclusão de uma nova movimentação do ativo.
     * 
     * @param newMovement
     */
    private void movementValidations(AssetMovement newMovement) {
    	// Caso de movimentação de venda, ocorre a verificação para a garantia de quantidade disponível do ativo para venda
    	if (MovementType.SELL.equals(newMovement.getType()) && newMovement.getQuantity()
    			.compareTo(this.getTotalQuantity(newMovement.getDate())) == 1) {
    		throw new AssetQuantityNotAvailable(ExceptionMessage.MESSAGE_ASSET_QUANTITY_NOT_AVAILABLE);
    	}
    	
    	//Movimentação só pode ocorrer entre entre a data de emissão (inclusive) e a data de vencimento	(exclusive)
    	if (this.isInvalidDateToMovement(newMovement.getDate())) {
    		throw new MovementNotAllowedInDate(ExceptionMessage.MESSAGE_MOVEMENT_NOT_ALLOWED_IN_DATE);
    	}
    	
    	//Movimentação pode ocorrer apenas de segunda-feira a sexta-feira. 
    	if (this.isWeekend(newMovement.getDate())) {
    		throw new MovementNotAllowedInDate(ExceptionMessage.MESSAGE_MOVEMENT_NOT_ALLOWED_IN_WEEKEND);
    	}
    	
    }
    
    /**
     * Método que compara a data de movimentação com as datas de emissão e vencimento
     * 
     * @param dateMovement
     * @return boolean
     */
    private boolean isInvalidDateToMovement(LocalDate dateMovement) {
    	if (dateMovement.isBefore(this.issueDate)) {
    		return true;
    	}
    	if (dateMovement.isAfter(this.dueDate) || dateMovement.isEqual(this.dueDate)) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * Método que verifica se a data de movimentação é fim de semana
     * 
     * @param dateMovement
     * @return boolean
     */
    private boolean isWeekend(LocalDate dateMovement) {
        DayOfWeek dayOfWeek = dateMovement.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
    

}
