package com.maps.financial.domain.asset;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {
	
	Asset findByName(String name);

}
