package com.maps.financial.domain.asset;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maps.financial.exceptions.AuthorizationException;
import com.maps.financial.exceptions.ExceptionMessage;
import com.maps.financial.exceptions.IssueDateNotBeforeDueDate;
import com.maps.financial.exceptions.ObjectNotFoundException;
import com.maps.financial.infra.security.SecurityUtils;

@Service
public class AssetService {
	
	@Autowired
	private AssetRepository repository;
	
	@Autowired
	private SecurityUtils securityUtils;
	
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	/**
	 * O ambiente disponibilizado deve conter os seguintes dados pré-cadastrados:
	 *	- Ativos com nomes de "ATIVO0" até "ATIVO127"
	 *	- Valores de mercado para todos os ativos acima para o dia 2020-01-02
	 */
	@PostConstruct
    public void init() {
		for (Integer i = 0; i < 128; i++) {
			preRegistration("ATIVO".concat(i.toString()));
		}
	}
	
	public Asset findById(final Long id)  throws ObjectNotFoundException {
		return repository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, Asset.class));
	}
	
	public List<Asset> findAll() {
		return repository.findAll();
	}
	
	public Asset create(final Asset asset) {
		//Validação: ativo só pode ser incluído por usuário com privilégio administrativo
		if (!isUserAdministrator()) {
			throw new AuthorizationException(ExceptionMessage.MESSAGE_ACCESS_DENIED);
		}
		
		//Validação: data de emissão deve ser sempre anterior a data de vencimento
		if (asset.getIssueDate() == null || asset.getDueDate() == null || 
				!asset.getIssueDate().isBefore(asset.getDueDate())) {
			throw new IssueDateNotBeforeDueDate(ExceptionMessage.MESSAGE_ISSUE_NOT_BEFORE_DUE);
		}
				
		return repository.save(asset);
	}
	
	public Asset update(final Long assetId, final Asset assetUpdate) {
		//Validação: ativo só pode ser alterado por usuário com privilégio administrativo
		if (!isUserAdministrator()) {
			throw new AuthorizationException(ExceptionMessage.MESSAGE_ACCESS_DENIED);
		}
		
		Asset asset = findById(assetId);
		asset.setName(assetUpdate.getName());
		asset.setType(assetUpdate.getType());
		return asset;
	}
	
	public void delete(final Long assetId) {
		//Validação: ativo só pode ser excluído por usuário com privilégio administrativo
		if (!isUserAdministrator()) {
			throw new AuthorizationException(ExceptionMessage.MESSAGE_ACCESS_DENIED);
		}
				
		Asset asset = findById(assetId);
		repository.delete(asset);
	}
	
	public Asset includeMovementByAssetId(final Long assetId, final AssetMovement newMovement) {
		Asset asset = findById(assetId);
		includeMovement(asset, newMovement);
		return asset;
	}
	
	public Asset includeMovementByAssetName(final String assetName, final AssetMovement newMovement) {
		Asset asset = findByName(assetName);
		includeMovement(asset, newMovement);
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
	
	public Asset excludeMarketPrice(final Long assetId, final String data) {
		findAll();
		LocalDate date = LocalDate.parse(data, formatter);
		Asset asset = findById(assetId);
		asset.excludeMarketPrice(date);
		return asset;
	}
	
	public List<AssetMovement> getMovements(Long assetId, String dataInicio, String dataFim) {
		LocalDate dateBegin = LocalDate.parse(dataInicio, formatter);
		LocalDate dateEnd = LocalDate.parse(dataFim, formatter);
		Asset asset = findById(assetId);
		List<AssetMovement> movements = asset.getMovements()
				.stream()
				.filter(movement -> !movement.getDate().isBefore(dateBegin) && !movement.getDate().isAfter(dateEnd)) 
				.collect(Collectors.toList());
		
		return movements;
	}
	
	private void includeMovement(final Asset asset, final AssetMovement newMovement) {
		//Validação: usuário administrativo não deve poder gerar lançamentos e movimentos
		if (isUserAdministrator()) {
			throw new AuthorizationException(ExceptionMessage.MESSAGE_ACCESS_DENIED);
		}
		
		newMovement.setAsset(asset);
		asset.includeMovement(newMovement);
	}
	
	private Asset findByName(String name) {
		return repository.findByName(name);
	}
	
	/**
	 * Método responsável por verificar se o usuário logado possui função de administrador
	 * 
	 * @param proceedByAdmin
	 * @return boolean
	 */
	private boolean isUserAdministrator() {
		return securityUtils.currentUserIsAdmin();
	}

	private void preRegistration(String name) {
		Asset asset = createAsset(name);
		asset.includeMarketPrice(new BigDecimal(10.00), LocalDate.of(2020, 1, 2));
		repository.save(asset);
	}
	
	private Asset createAsset(String name) {
		return Asset.builder()
				.name(name)
				.type(AssetType.RF)
				.issueDate(LocalDate.of(2020, 1, 1))
				.dueDate(LocalDate.of(2020, 12, 31))
				.build();
	}

}
