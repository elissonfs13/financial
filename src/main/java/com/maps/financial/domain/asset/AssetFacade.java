package com.maps.financial.domain.asset;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.maps.financial.domain.account.AccountFacade;

/**
 * Classe Façade para Ativos Financeiros
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
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
	
	/**
	 * Chamada para inclusão de lançamento da conta corrente do usuário logado e movimentação no ativo com id especificado
	 * Compras e vendas devem gerar lançamentos na conta corrente refletindo o valor gasto ou ganho.
	 * 
	 * @param assetId
	 * @param newMovement
	 * @return Asset
	 */
	@Transactional
	public Asset includeMovement(final Long assetId, final AssetMovement newMovement) {
		accountFacade.includeLaunch(newMovement);
		return service.includeMovementByAssetId(assetId, newMovement);
	}
	
	/**
	 * Chamada para inclusão de lançamento da conta corrente do usuário logado e movimentação no ativo com nome especificado
	 * Compras e vendas devem gerar lançamentos na conta corrente refletindo o valor gasto ou ganho.
	 * 
	 * @param assetName
	 * @param newMovement
	 * @return Asset
	 */
	@Transactional
	public Asset includeMovement(final String assetName, final AssetMovement newMovement) {
		accountFacade.includeLaunch(newMovement);
		return service.includeMovementByAssetName(assetName, newMovement);
	}
	
	public BigDecimal getTotalQuantity(final Long assetId, LocalDate date) {
		return service.getTotalQuantity(assetId, date);
	}
	
	public BigDecimal getTotalMarketPrice(final Long assetId, LocalDate date) {
		return service.getTotalMarketPrice(assetId, date);
	}
	
	public BigDecimal getIncome(final Long assetId, LocalDate date) {
		return service.getIncome(assetId, date);
	}
	
	public BigDecimal getProfit(final Long assetId, LocalDate date) {
		return service.getProfit(assetId, date);
	}
	
	@Transactional
	public Asset includeMarketPrice(final Long assetId, final BigDecimal price, final LocalDate date) {
		return service.includeMarketPrice(assetId, price, date);
	}
	
	@Transactional
	public Asset excludeMarketPrice(final Long assetId, final String data) {
		return service.excludeMarketPrice(assetId, data);
	}
	
	public List<AssetMovement> getMovements(Long assetId, String dataInicio, String dataFim) {
		return service.getMovements(assetId, dataInicio, dataFim);
	}

}
