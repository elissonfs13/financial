package com.maps.financial.domain.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

/**
 * Factory para lançamentos da conta corrente
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
@Component
public class LaunchFactory {
	
	/**
	 * Criação de um novo lançamento para a conta corrente com o sparâmetros recebidos
	 * 
	 * @param description
	 * @param type
	 * @param value
	 * @param date
	 * @return Launch
	 */
	public Launch generateLaunch(String description, LaunchType type, BigDecimal value, LocalDate date) {
		return Launch.builder()
				.description(description)
				.type(type)
				.value(value)
				.date(date)
				.build();
	}

}
