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
import com.maps.financial.resources.asset.dto.AssetMovementDTO;

@RestController
@RequestMapping("/movimentacao")
public class AssetMovementResource {
	//TODO: Ajustar API na fase 3
	@Autowired
	private AssetFacade assetFacade;
	
	@Autowired
	private ModelMapper modelMapper;
	
	/**
	 * Endpoint REST para movimentação de compra
	 * 
	 * @param assetDTO
	 * @return AssetDTO
	 */
	@PostMapping("/compra")
	public ResponseEntity<AssetMovementDTO> movementBuy(@RequestBody final AssetMovementDTO assetMovementDTO) {
		final Asset movement = assetFacade.includeMovement(1L, 1L, modelMapper.map(assetMovementDTO, AssetMovement.class));
		final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(movement.getId()).toUri();
		return ResponseEntity
				.created(uri)
				.body(modelMapper.map(movement, AssetMovementDTO.class));
	}
	
	/**
	 * Endpoint REST para movimentação de venda
	 * 
	 * @param assetDTO
	 * @return AssetDTO
	 */
	@PostMapping("/venda")
	public ResponseEntity<AssetMovementDTO> movementSell(@RequestBody final AssetMovementDTO assetMovementDTO) {
		final Asset movement = assetFacade.includeMovement(1L, 1L, modelMapper.map(assetMovementDTO, AssetMovement.class));
		final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(movement.getId()).toUri();
		return ResponseEntity
				.created(uri)
				.body(modelMapper.map(movement, AssetMovementDTO.class));
	}

}
