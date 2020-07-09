package com.maps.financial.domain.asset;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.maps.financial.domain.account.AccountFacade;

@Component
public class AssetFacade {
	
	@Autowired
	private AssetService service;
	
	@Autowired
	private AccountFacade accountFacade;
	
	public Asset findById(final Long id){
		return service.findById(id);
	}
	
	public List<Asset> findAll() {
		return service.findAll();
	}
	
	@Transactional
	public Asset create(final Asset asset) {
		return service.create(asset);
	}
	
	@Transactional
	public Asset update(final Long assetId, final Asset assetUpdate) {
		return service.update(assetId, assetUpdate);
	}
	
	@Transactional
	public void delete(final Long assetId) {
		service.delete(assetId);
	}
	
	@Transactional
	public Asset includeMovement(final Long accountId, final Long assetId, final AssetMovement newMovement) {
		accountFacade.includeLaunch(accountId, newMovement);
		return service.includeMovement(assetId, newMovement);
	}

}
