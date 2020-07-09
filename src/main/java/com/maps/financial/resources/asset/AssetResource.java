package com.maps.financial.resources.asset;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.maps.financial.domain.asset.Asset;
import com.maps.financial.domain.asset.AssetFacade;
import com.maps.financial.domain.asset.AssetMovement;
import com.maps.financial.resources.asset.dto.AssetDTO;
import com.maps.financial.resources.asset.dto.AssetMovementDTO;
import com.maps.financial.resources.asset.dto.MarketPriceDTO;

@RestController
@RequestMapping("/ativo")
public class AssetResource {
	
	@Autowired
	private AssetFacade assetFacade;
	
	@Autowired
	private ModelMapper modelMapper;
	
	/**
	 * Endpoint REST para consulta de posição dos ativos
	 * 
	 * @return List<AssetDTO>
	 */
	@GetMapping("/posicao")
	public ResponseEntity<List<AssetDTO>> findAssetsPosition() {
		final List<Asset> assets = assetFacade.findAll();
		return ResponseEntity.ok()
				.body(assets.stream()
						.map(asset -> modelMapper.map(asset, AssetDTO.class))
						.collect(Collectors.toList()));
	}
	
	/**
	 * Endpoint REST para busca de um ativo específico
	 * 
	 * @param assetId
	 * @return AssetDTO
	 */
	@GetMapping("/{assetId}")
	public ResponseEntity<AssetDTO> findById(@PathVariable final Long assetId) {
		final Asset asset = assetFacade.findById(assetId);
		final AssetDTO assetDTO = modelMapper.map(asset, AssetDTO.class);
		return ResponseEntity.ok().body(assetDTO);
	}
	
	/**
	 * Endpoint REST para cadastro de um novo ativo
	 * 
	 * @param assetDTO
	 * @return AssetDTO
	 */
	@PostMapping
	public ResponseEntity<AssetDTO> create(@RequestBody final AssetDTO assetDTO) {
		final Asset asset = assetFacade.create(modelMapper.map(assetDTO, Asset.class));
		final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(asset.getId()).toUri();
		return ResponseEntity
				.created(uri)
				.body(modelMapper.map(asset, AssetDTO.class));
	}
	
	/**
	 * Endpoint REST para alteração de um ativo específico
	 * 
	 * @param assetIdO
	 * @param assetDTO
	 * @return AssetDTO
	 */
	@PutMapping("/{assetId}")
	public ResponseEntity<AssetDTO> update(@PathVariable final Long assetId, @RequestBody final AssetDTO assetDTO) {
		final Asset asset = assetFacade.update(assetId, modelMapper.map(assetDTO, Asset.class));
		return ResponseEntity.ok().body(modelMapper.map(asset, AssetDTO.class));
	}
	
	/**
	 * Endpoint REST para exclusão de um ativo específico
	 * 
	 * @param assetId
	 */
	@DeleteMapping("/{assetId}")
	public ResponseEntity<Void> delete(@PathVariable final Long assetId) {
		assetFacade.delete(assetId);
		return ResponseEntity.ok().build();
	}
	
	/**
	 * Endpoint REST para adição de um novo valor de mercado em data determinada para um ativo específico
	 * 
	 * @param assetId
	 * @param marketPriceDTO
	 * @return AssetDTO
	 */
	@PutMapping("/{assetId}/adiciona-valor-mercado")
	public ResponseEntity<AssetDTO> includeMarketPrice(@PathVariable final Long assetId, @RequestBody final MarketPriceDTO marketPriceDTO) {
		final Asset asset = assetFacade.includeMarketPrice(assetId, marketPriceDTO.getPrice(), marketPriceDTO.getDate());
		return ResponseEntity.ok().body(modelMapper.map(asset, AssetDTO.class));
	}
	
	/**
	 * Endpoint REST para exclusão de valor de mercado de data determinada para um ativo específico
	 * 
	 * @param assetId
	 * @param marketPriceDTO
	 * @return AssetDTO
	 */
	@PutMapping("/{assetId}/exclui-valor-mercado")
	public ResponseEntity<AssetDTO> excludeMarketPrice(@PathVariable final Long assetId, @RequestBody final MarketPriceDTO marketPriceDTO) {
		final Asset asset = assetFacade.excludeMarketPrice(assetId, marketPriceDTO.getDate());
		return ResponseEntity.ok().body(modelMapper.map(asset, AssetDTO.class));
	}
	
	/**
	 * Endpoint REST para cadastro de um novo movimento
	 * 
	 * @param assetDTO
	 * @return AssetDTO
	 */
	@PostMapping("/{assetId}/movimentacao")
	public ResponseEntity<AssetDTO> includeMovement(@PathVariable final Long assetId, @RequestBody final AssetMovementDTO assetMovementDTO) {
		final Asset asset = assetFacade.includeMovement(1L, assetId, modelMapper.map(assetMovementDTO, AssetMovement.class));
		final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(asset.getId()).toUri();
		return ResponseEntity
				.created(uri)
				.body(modelMapper.map(asset, AssetDTO.class));
	}

}
