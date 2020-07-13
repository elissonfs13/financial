package com.maps.financial.domain.asset;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface para operações com Ativos Financeiros
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
public interface AssetRepository extends JpaRepository<Asset, Long> {
	
	Asset findByName(String name);

}
