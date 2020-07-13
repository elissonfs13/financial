package com.maps.financial.resources.asset;

import java.net.URI;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.maps.financial.domain.asset.Asset;
import com.maps.financial.domain.asset.AssetFacade;
import com.maps.financial.domain.asset.AssetMovement;
import com.maps.financial.domain.asset.MovementType;
import com.maps.financial.resources.asset.dto.AssetDTO;
import com.maps.financial.resources.asset.dto.AssetMovementDTO;

/**
 * Resource para Movimentação de Ativo
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
@RestController
@RequestMapping("/movimentacao")
public class AssetMovementResource {
	
	@Autowired
	private AssetFacade assetFacade;
	
	@Autowired
	private ModelMapper modelMapper;
	
	/**
	 * Endpoint REST para movimentação de compra
	 * 
	 * @param assetMovementDTO
	 * @return AssetMovementDTO
	 */
	@PostMapping("/compra")
	public ResponseEntity<AssetDTO> movementBuy(@RequestBody final AssetMovementDTO assetMovementDTO) {
		AssetMovement newAssetMovement = modelMapper.map(assetMovementDTO, AssetMovement.class);
		newAssetMovement.setType(MovementType.BUY);
		final Asset asset = assetFacade.includeMovement(assetMovementDTO.getAtivo(), newAssetMovement);
		final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(asset.getId()).toUri();
		return ResponseEntity
				.created(uri)
				.body(modelMapper.map(asset, AssetDTO.class));
	}
	
	/**
	 * Endpoint REST para movimentação de venda
	 * 
	 * @param assetMovementDTO
	 * @return AssetMovementDTO
	 */
	@PostMapping("/venda")
	public ResponseEntity<AssetDTO> movementSell(@RequestBody final AssetMovementDTO assetMovementDTO) {
		AssetMovement newAssetMovement = modelMapper.map(assetMovementDTO, AssetMovement.class);
		newAssetMovement.setType(MovementType.SELL);
		final Asset asset = assetFacade.includeMovement(assetMovementDTO.getAtivo(), newAssetMovement);
		final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(asset.getId()).toUri();
		return ResponseEntity
				.created(uri)
				.body(modelMapper.map(asset, AssetDTO.class));
	}

}
