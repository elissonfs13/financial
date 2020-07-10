package com.maps.financial.domain.asset;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maps.financial.exceptions.ExceptionMessage;
import com.maps.financial.exceptions.IssueDateNotBeforeDueDate;
import com.maps.financial.exceptions.ObjectNotFoundException;

@Service
public class AssetService {
	
	@Autowired
	private AssetRepository repository;
	
	public Asset findById(final Long id)  throws ObjectNotFoundException {
		return repository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, Asset.class));
	}
	
	public List<Asset> findAll() {
		return repository.findAll();
	}
	
	public Asset create(final Asset asset) {
		//Validação: data de emissão deve ser sempre anterior a data de vencimento
		if (asset.getIssueDate() == null || asset.getDueDate() == null || 
				!asset.getIssueDate().isBefore(asset.getDueDate())) {
			throw new IssueDateNotBeforeDueDate(ExceptionMessage.MESSAGE_ISSUE_NOT_BEFORE_DUE);
		}
				
		return repository.save(asset);
	}
	
	public Asset update(final Long assetId, final Asset assetUpdate) {
		Asset asset = findById(assetId);
		asset.setName(assetUpdate.getName());
		asset.setType(assetUpdate.getType());
		return asset;
	}
	
	public void delete(final Long assetId) {
		Asset asset = findById(assetId);
		repository.delete(asset);
	}
	
	public Asset includeMovement(final Long assetId, final AssetMovement newMovement) {
		Asset asset = findById(assetId);
		asset.includeMovement(newMovement);
		return asset;
	}
	
	public BigDecimal getTotalQuantity(final Long assetId, LocalDate atualDate) {
		Asset asset = findById(assetId);
		return asset.getTotalQuantity(atualDate);
	}
	
	public BigDecimal getTotalMarketPrice(final Long assetId, LocalDate atualDate) {
		Asset asset = findById(assetId);
		return asset.getTotalMarketPrice(atualDate);
	}
	
	public BigDecimal getIncome(final Long assetId, LocalDate atualDate) {
		Asset asset = findById(assetId);
		return asset.getIncome(atualDate);
	}
	
	public BigDecimal getProfit(final Long assetId, LocalDate atualDate) {
		Asset asset = findById(assetId);
		return asset.getProfit(atualDate);
	}
	
	public Asset includeMarketPrice(final Long assetId, final BigDecimal price, final LocalDate date) {
		Asset asset = findById(assetId);
		asset.includeMarketPrice(price, date);
		return asset;
	}
	
	public Asset excludeMarketPrice(final Long assetId, final LocalDate date) {
		Asset asset = findById(assetId);
		asset.excludeMarketPrice(date);
		return asset;
	}
	
	public List<AssetMovement> getMovements(Long assetId, LocalDate dateBegin, LocalDate dateEnd) {
		Asset asset = findById(assetId);
		List<AssetMovement> movements = asset.getMovements()
				.stream()
				.filter(movement -> !movement.getDate().isBefore(dateBegin) && !movement.getDate().isAfter(dateEnd)) 
				.collect(Collectors.toList());
		
		return movements;
	}

}
