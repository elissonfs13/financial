package com.maps.financial.domain.account;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

@Entity
@Table(name = "launch")
@Getter
@AllArgsConstructor @NoArgsConstructor @Builder
public class Launch {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Enumerated(EnumType.STRING)
	private LaunchType type;
	
	private String description;
	
	private BigDecimal value;
	
	@JoinColumn(name = "account_id", referencedColumnName = "id")
	@ManyToOne
	private Account account;
	
	@Basic(optional = false)
	@Temporal(TemporalType.DATE)
	private LocalDate date; //data do movimento
	
	public BigDecimal getValue() {
		return this.value.setScale(2, BigDecimal.ROUND_DOWN);
	}

}
