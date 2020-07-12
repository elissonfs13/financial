package com.maps.financial.domain.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.maps.financial.domain.user.User;
import com.maps.financial.exceptions.AccountBalanceNotAvailable;
import com.maps.financial.exceptions.ExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account")
@Getter
@AllArgsConstructor @NoArgsConstructor @Builder
public class Account {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Setter
	@Column(precision=10, scale=2)
	private BigDecimal balance;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "account", orphanRemoval = true)
	private final List<Launch> launches = new ArrayList<>();
	
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false)
    private User user;
	
	/**
	 * Método responsável por subtrair o saldo da conta pelo valor do novo lançamento.
	 * Inclui verificação para saldo nunca ficar negativo
	 * 
	 * @param newValue
	 */
	private void updateBalanceOutbound(BigDecimal newValue) {
		if (this.isUnavailableBalance(newValue)) {
			throw new AccountBalanceNotAvailable(ExceptionMessage.MESSAGE_ACCOUNT_BALANACE_NOT_AVAILABLE);
		}
		this.balance.subtract(newValue);
	}
	
	/**
	 * Método responsável por somar o saldo da conta com o valor do novo lançamento
	 * 
	 * @param newValue
	 */
	private void updateBalanceInbound(BigDecimal newValue) {
		this.balance.add(newValue);
	}
	
	/**
	 * Método responsável por verificar se a conta não possui saldo atual capaz de suportar a operação.
	 * Somente deverá verificada em lançamentos de saída
	 * 
	 * @param newValue
	 * @return boolean
	 */
	private boolean isUnavailableBalance(BigDecimal newValue) {
		return this.getBalance().compareTo(newValue) == -1 ? true : false;
	}
	
	/**
     * Método responsável por retornar o valor total das movimentações de saída na conta até a data informada
     * 
     * @param atualDate
     * @return BigDecimal
     */
    private BigDecimal getTotalValueOutbound(LocalDate atualDate) {
    	if (atualDate == null) {
    		return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
    	}
    	BigDecimal totalValue = this.getLaunches()
    			.stream()
    			.filter(launch -> LaunchType.OUTBOUND.equals(launch.getType()) && !atualDate.isBefore(launch.getDate()))
    			.map(launch -> launch.getValue())
    			.reduce(BigDecimal.ZERO, BigDecimal::add);
    	
    	return totalValue.setScale(2, BigDecimal.ROUND_DOWN);
    }
    
    /**
     * Método responsável por retornar o valor total das movimentações de entrada na conta até a data atual
     * 
     * @param atualDate
     * @return BigDecimal
     */
    private BigDecimal getTotalValueInbound(LocalDate atualDate) {
    	if (atualDate == null) {
    		return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
    	}
    	BigDecimal totalValue = this.getLaunches()
    			.stream()
    			.filter(launch -> LaunchType.INBOUND.equals(launch.getType()) && !atualDate.isBefore(launch.getDate()))
    			.map(launch -> launch.getValue())
    			.reduce(BigDecimal.ZERO, BigDecimal::add);
    	
    	return totalValue.setScale(2, BigDecimal.ROUND_DOWN);
    }
	
	/**
	 * Método responsável por receber novo lançamento e adicionar (caso puder) na lista de lançamentos
	 * 
	 * @param newLaunch
	 */
	public void includeLaunch(Launch newLaunch) {
		if (LaunchType.OUTBOUND.equals(newLaunch.getType())) {
			this.updateBalanceOutbound(newLaunch.getValue());
		} else {
			this.updateBalanceInbound(newLaunch.getValue());
		}
		newLaunch.setAccount(this);
		this.launches.add(newLaunch);
	}
	
	/**
	 * Método responsável por calcular e retornar o saldo disponível na conta em uma determinada data
	 * 
	 * @param atualDate
	 * @return BigDecimal
	 */
	public BigDecimal getBalanceInDate(LocalDate atualDate) {
		BigDecimal balanceInDate = this.getBalance()
				.add(this.getTotalValueInbound(atualDate))
				.subtract(this.getTotalValueOutbound(atualDate));
		
		return balanceInDate.setScale(2, BigDecimal.ROUND_DOWN);
	}

}
