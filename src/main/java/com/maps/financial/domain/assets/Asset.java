package com.maps.financial.domain.assets;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "financial_asset")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor @NoArgsConstructor @Builder
public class Asset {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@EqualsAndHashCode.Include
    private Integer id;
 
    private String name;
    
    @Column(precision=15, scale=8)
    private BigDecimal marketPrice;
 
    @Enumerated(EnumType.STRING)
    private AssetType type;

}
