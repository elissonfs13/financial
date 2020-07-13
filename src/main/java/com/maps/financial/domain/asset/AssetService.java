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

/**
 * Classe de serviços para Ativo
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
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
	
	/**
	 * Busca pelo ativo que contém o id especificado
	 * 
	 * @param id
	 * @return Asset
	 * @throws ObjectNotFoundException
	 */
	public Asset findById(final Long id)  throws ObjectNotFoundException {
		return repository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, Asset.class));
	}
	
	/**
	 * Busca por todos os ativos cadastrados
	 * 
	 * @return List<Asset>
	 */
	public List<Asset> findAll() {
		return repository.findAll();
	}
	
	/**
	 * Criação de um novo ativo
	 * 
	 * @param asset
	 * @return Asset
	 */
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
	
	/**
	 * Atualização do ativo que contém o id especificado
	 * 
	 * @param assetId
	 * @param assetUpdate
	 * @return Asset
	 */
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
	
	/**
	 * Exclusão do ativo que contém o id especificado
	 * 
	 * @param assetId
	 */
	public void delete(final Long assetId) {
		//Validação: ativo só pode ser excluído por usuário com privilégio administrativo
		if (!isUserAdministrator()) {
			throw new AuthorizationException(ExceptionMessage.MESSAGE_ACCESS_DENIED);
		}
				
		Asset asset = findById(assetId);
		repository.delete(asset);
	}
	
	/**
	 * Inclui uma nova movimentação no ativo que contém o id especificado
	 * 
	 * @param assetId
	 * @param newMovement
	 * @return Asset
	 */
	public Asset includeMovementByAssetId(final Long assetId, final AssetMovement newMovement) {
		Asset asset = findById(assetId);
		includeMovement(asset, newMovement);
		return asset;
	}
	
	/**
	 * Inclui uma nova movimentação no ativo que contém o nome especificado
	 * 
	 * @param assetName
	 * @param newMovement
	 * @return Asset
	 */
	public Asset includeMovementByAssetName(final String assetName, final AssetMovement newMovement) {
		Asset asset = findByName(assetName);
		includeMovement(asset, newMovement);
		return asset;
	}
	
	/**
	 * Retorna a quantidade total até a data informada do ativo que contém o id especificado
	 * Quantidade total: soma das quantidades compradas menos as quantidades vendidas do ativo
	 * 
	 * @param assetId
	 * @param atualDate
	 * @return BigDecimal
	 */
	public BigDecimal getTotalQuantity(final Long assetId, LocalDate atualDate) {
		Asset asset = findById(assetId);
		return asset.getTotalQuantity(atualDate);
	}
	
	/**
	 * Retorna o valor de mercado total até a data informada do ativo que contém o id especificado 
	 * Valor de mercado total: quantidade total multiplicada pelo preço de mercado do ativo
	 * 
	 * @param assetId
	 * @param atualDate
	 * @return BigDecimal
	 */
	public BigDecimal getTotalMarketPrice(final Long assetId, LocalDate atualDate) {
		Asset asset = findById(assetId);
		return asset.getTotalMarketPrice(atualDate);
	}
	
	/**
	 * Retorna o rendimento até a data informada do ativo que contém o id especificado 
	 * Rendimento: preço de mercado dividido pelo preço médio das compras
	 * 
	 * @param assetId
	 * @param atualDate
	 * @return BigDecimal
	 */
	public BigDecimal getIncome(final Long assetId, LocalDate atualDate) {
		Asset asset = findById(assetId);
		return asset.getIncome(atualDate);
	}
	
	/**
	 * Retorna o lucro até a data informada do ativo que contém o id especificado 
	 * Lucro: soma dos valores das vendas menos os valores das compras do ativo
	 * 
	 * @param assetId
	 * @param atualDate
	 * @return BigDecimal
	 */
	public BigDecimal getProfit(final Long assetId, LocalDate atualDate) {
		Asset asset = findById(assetId);
		return asset.getProfit(atualDate);
	}
	
	/**
	 * Definição de um novo preço de mercado para o ativo que possui o id especificado
	 * 
	 * @param assetId
	 * @param price
	 * @param date
	 * @return Asset
	 */
	public Asset includeMarketPrice(final Long assetId, final BigDecimal price, final LocalDate date) {
		Asset asset = findById(assetId);
		asset.includeMarketPrice(price, date);
		return asset;
	}
	
	/**
	 * Exclusão do preço de mercado que possui data especificada para o ativo que possui o id especificado
	 * 
	 * @param assetId
	 * @param data
	 * @return Asset
	 */
	public Asset excludeMarketPrice(final Long assetId, final String data) {
		LocalDate date = LocalDate.parse(data, formatter);
		Asset asset = findById(assetId);
		asset.excludeMarketPrice(date);
		return asset;
	}
	
	/**
	 * Retorna a lista de movimentações entre as datas informadas do ativo que possui o id especificado
	 * Consultas de lançamentos, movimentações devem ter filtro obrigatório "data início" e "data fim", 
	 * 		filtrando a data de movimento (inclusive/inclusive).
	 * 
	 * @param assetId
	 * @param dataInicio
	 * @param dataFim
	 * @return List<AssetMovement>
	 */
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
	
	/**
	 * Inclui uma nova movimentação no ativo especificado
	 * 
	 * @param assetId
	 * @param newMovement
	 */
	private void includeMovement(final Asset asset, final AssetMovement newMovement) {
		//Validação: usuário administrativo não deve poder gerar lançamentos e movimentos
		if (isUserAdministrator()) {
			throw new AuthorizationException(ExceptionMessage.MESSAGE_ACCESS_DENIED);
		}
		
		newMovement.setAsset(asset);
		asset.includeMovement(newMovement);
	}
	
	/**
	 * Busca pelo ativo que contém o nome especificado
	 * 
	 * @param name
	 * @return
	 */
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

	/**
	 * Desenvolvido para realização do pré cadastro de ativos
	 * 
	 * @param name
	 */
	private void preRegistration(String name) {
		Asset asset = createAsset(name);
		asset.includeMarketPrice(new BigDecimal(10.00), LocalDate.of(2020, 1, 2));
		repository.save(asset);
	}
	
	/**
	 * Desenvolvido para realização do pré cadastro de ativos
	 * 
	 * @param name
	 * @return Asset
	 */
	private Asset createAsset(String name) {
		return Asset.builder()
				.name(name)
				.type(AssetType.RF)
				.issueDate(LocalDate.of(2020, 1, 1))
				.dueDate(LocalDate.of(2020, 12, 31))
				.build();
	}

}
