package com.maps.financial.domain.account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.maps.financial.exceptions.AccountBalanceNotAvailable;
import com.maps.financial.exceptions.ExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @NoArgsConstructor @Builder
public class Account {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@EqualsAndHashCode.Include
    private Long id;
	
	@Column(precision=10, scale=2)
	private BigDecimal balance;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "account", orphanRemoval = true)
	private final List<Launch> launches = new ArrayList<>();
	
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
	 * Método responsável por receber novo lançamento e adicionar (caso puder) na lista de lançamentos
	 * 
	 */
	public void includeLaunch(Launch newLaunch) {
		if (LaunchType.OUTBOUND.equals(newLaunch.getType())) {
			this.updateBalanceOutbound(newLaunch.getValue());
		} else {
			this.updateBalanceInbound(newLaunch.getValue());
		}
		this.launches.add(newLaunch);
	}

}
