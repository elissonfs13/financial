package com.maps.financial.domain.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

@Component
public class LaunchFactory {
	
	public Launch generateLaunch(String description, LaunchType type, BigDecimal value, LocalDate date) {
		return Launch.builder()
				.description(description)
				.type(type)
				.value(value)
				.date(date)
				.build();
	}

}
