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

import com.maps.financial.exceptions.AssetQuantityNotAvailable;
import com.maps.financial.exceptions.ExceptionMessage;
import com.maps.financial.exceptions.MovementNotAllowedInDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * Classe de Ativos Financeiros
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
@Entity
@Table(name = "financial_asset")
@Getter
@AllArgsConstructor @NoArgsConstructor @Builder
public class Asset {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
 
	@Setter
	@Basic(optional = false)
	@Column(unique=true)
    private String name;
 
	@Setter
	@Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private AssetType type;
	
	@Setter
	@Basic(optional = false)
	@Column(name = "issue_date", columnDefinition = "DATE")
	private LocalDate issueDate; //data de emissão
	
	@Setter
	@Basic(optional = false)
	@Column(name = "due_date", columnDefinition = "DATE")
	private LocalDate dueDate; //data de vencimento
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "asset", orphanRemoval = true)
	private final List<AssetMovement> movements = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "asset", orphanRemoval = true)
	private final List<MarketPrice> marketPrices = new ArrayList<>();
    
    /**
     * Método responsável por calcular e retornar a quantidade total atual do ativo
     * Quantidade total: soma das quantidades compradas menos as quantidades vendidas do ativo
     * 
     * @param date 
     * @return BigDecimal
     */
    public BigDecimal getTotalQuantity(LocalDate date) {
    	return this.getQuantityBuy(date).subtract(this.getQuantitySell(date));
    }
    
    /**
     * Método responsável por calcular e retornar o valor de mercado total atual do ativo
     * Valor de mercado total: quantidade total multiplicada pelo preço de mercado do ativo
     * 
     * @param date 
     * @return BigDecimal
     */
    public BigDecimal getTotalMarketPrice(LocalDate date) {
    	BigDecimal totalMarketPrice = this.getTotalQuantity(date).multiply(this.getMarketPrice());
    	return totalMarketPrice.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por calcular e retornar o valor do rendimento 
     * Rendimento: preço de mercado dividido pelo preço médio das compras
     * 
     * @param date 
     * @return BigDecimal
     */
    public BigDecimal getIncome(LocalDate date) {
    	BigDecimal averageValueBuy = this.getAverageValueBuy(date);
    	if (BigDecimal.ZERO.compareTo(averageValueBuy) == 0) {
    		return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
    	}
    	BigDecimal income = this.getMarketPrice().divide(averageValueBuy, BigDecimal.ROUND_DOWN);
    	return income.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por calcular e retornar o valor do lucro 
     * Lucro: soma dos valores das vendas menos os valores das compras do ativo
     * 
     * @param date 
     * @return BigDecimal
     */
    public BigDecimal getProfit(LocalDate date) {
    	BigDecimal profit = this.getTotalValueSell(date).subtract(this.getTotalValueBuy(date)); 
    	return profit.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por adicionar uma nova movimentação do ativo após passar pelas validações
     * 
     * @param newMovement
     */
    public synchronized void includeMovement(AssetMovement newMovement) {
    	this.movementValidations(newMovement);
    	this.movements.add(newMovement);
    }
    
    /**
     * Método responsável por adicionar um valor de mercado ao ativo
     * 
     * @param price
     * @param date
     */
    public synchronized void includeMarketPrice(BigDecimal price, LocalDate date) {
    	if (price != null && date != null) {
	    	MarketPrice marketPrice = MarketPrice.builder()
	    			.price(price.setScale(8, BigDecimal.ROUND_DOWN))
	    			.date(date)
	    			.asset(this)
	    			.build();
	    	
	    	this.marketPrices.add(marketPrice);
    	}
    }
    
    /**
     * Método responsável por excluir os valores de mercado do ativo em uma determinada data 
     * 
     * @param date
     */
    public synchronized void excludeMarketPrice(LocalDate date) {
    	List<MarketPrice> marketPricesFind = this.marketPrices
    			.stream()
    			.filter(p -> date.isEqual(p.getDate()))
    			.collect(Collectors.toList());
    	
    	for (MarketPrice marketPrice : marketPricesFind) {
    		this.marketPrices.remove(marketPrice);
    	}
    }
    
    /**
     * Método responsável por retornar a quantidade total de compras do ativo até a data informada
     * @param date 
     * 
     * @param date 
     * @return BigDecimal
     */
    private BigDecimal getQuantityBuy(LocalDate date) {
    	if (date == null) {
    		return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
    	}
    	BigDecimal totalQuantity = this.getMovements()
    			.stream()
    			.filter(movement -> MovementType.BUY.equals(movement.getType()) && !date.isBefore(movement.getDate()))
    			.map(movement -> movement.getQuantity())
    			.reduce(BigDecimal.ZERO, BigDecimal::add);		
    	
    	return totalQuantity.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por retornar o valor total de compras do ativo até a data informada
     * @param date 
     * 
     * @param date 
     * @return BigDecimal
     */
    private BigDecimal getTotalValueBuy(LocalDate date) {
    	if (date == null) {
    		return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
    	}
    	BigDecimal totalValue = this.getMovements()
    			.stream()
    			.filter(movement -> MovementType.BUY.equals(movement.getType()) && !date.isBefore(movement.getDate()))
    			.map(movement -> movement.getValue())
    			.reduce(BigDecimal.ZERO, BigDecimal::add);
    	
    	return totalValue.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por retornar a quantidade total de vendas do ativo até a data informada
     * @param date 
     * 
     * @param date 
     * @return BigDecimal
     */
    private BigDecimal getQuantitySell(LocalDate date) {
    	if (date == null) {
    		return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
    	}
    	BigDecimal totalQuantity = this.getMovements()
    			.stream()
    			.filter(movement -> MovementType.SELL.equals(movement.getType()) && !date.isBefore(movement.getDate()))
    			.map(movement -> movement.getQuantity())
    			.reduce(BigDecimal.ZERO, BigDecimal::add);		
    	
    	return totalQuantity.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por retornar o valor total de vendas do ativo até a data informada
     * 
     * @param date 
     * @return BigDecimal
     */
    private BigDecimal getTotalValueSell(LocalDate date) {
    	if (date == null) {
    		return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
    	}
    	BigDecimal totalValue = this.getMovements()
    			.stream()
    			.filter(movement -> MovementType.SELL.equals(movement.getType()) && !date.isBefore(movement.getDate()))
    			.map(movement -> movement.getValue())
    			.reduce(BigDecimal.ZERO, BigDecimal::add);
    	
    	return totalValue.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por calcular e retornar o preço médio das compras
     * 
     * @param date 
     * @return BigDecimal
     */
    private BigDecimal getAverageValueBuy(LocalDate date) {
    	BigDecimal quantityBuy = this.getQuantityBuy(date);
    	if (BigDecimal.ZERO.compareTo(quantityBuy) == 0) {
    		return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
    	}
		BigDecimal averageValueBuy = this.getTotalValueBuy(date).divide(quantityBuy, BigDecimal.ROUND_DOWN);
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
    	return marketPrice != null ? marketPrice.getPrice() : BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável pela realização das devidas validações para a inclusão de uma nova movimentação do ativo.
     * 
     * @param newMovement
     */
    private void movementValidations(AssetMovement newMovement) {
    	// Caso de movimentação de venda, ocorre a verificação para a garantia de quantidade disponível do ativo para venda na data atual
    	if ((MovementType.SELL.equals(newMovement.getType()) && 
    			newMovement.getQuantity().compareTo(this.getTotalQuantity(LocalDate.now())) == 1)) {
    		throw new AssetQuantityNotAvailable(ExceptionMessage.MESSAGE_ASSET_QUANTITY_NOT_AVAILABLE);
    	}
    	
    	//Movimentação só pode ocorrer entre a data de emissão (inclusive) e a data de vencimento	(exclusive)
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
