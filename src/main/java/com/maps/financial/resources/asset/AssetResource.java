package com.maps.financial.resources.asset;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.maps.financial.domain.asset.Asset;
import com.maps.financial.domain.asset.AssetFacade;
import com.maps.financial.domain.asset.AssetMovement;
import com.maps.financial.resources.asset.dto.AssetDTO;
import com.maps.financial.resources.asset.dto.AssetMovementDTO;
import com.maps.financial.resources.asset.dto.MarketPriceDTO;
import com.maps.financial.resources.asset.dto.PositionDTO;

@RestController
@RequestMapping("/ativo")
public class AssetResource {
	
	@Autowired
	private AssetFacade assetFacade;
	
	@Autowired
	private ModelMapper modelMapper;
	
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	/**
	 * Endpoint REST para consulta de posição dos ativos
	 * 
	 * @return List<AssetDTO>
	 */
	@GetMapping("/posicao")
	public ResponseEntity<List<PositionDTO>> findAssetsPosition(@RequestParam("data") String data) {
		final List<Asset> assets = assetFacade.findAll();
		return ResponseEntity.ok().body(createPositions(assets, data));
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
	@PostMapping("/{assetId}/adiciona-valor-mercado")
	public ResponseEntity<AssetDTO> includeMarketPrice(@PathVariable final Long assetId, @RequestBody final MarketPriceDTO marketPriceDTO) {
		final Asset asset = assetFacade.includeMarketPrice(assetId, marketPriceDTO.getPrice(), marketPriceDTO.getDate());
		final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(asset.getId()).toUri();
		return ResponseEntity
				.created(uri)
				.body(modelMapper.map(asset, AssetDTO.class));
	}
	
	/**
	 * Endpoint REST para exclusão de valor de mercado de data determinada para um ativo específico
	 * 
	 * @param assetId
	 * @param marketPriceDTO
	 * @return AssetDTO
	 */
	@PutMapping("/{assetId}/exclui-valor-mercado")
	public ResponseEntity<AssetDTO> excludeMarketPrice(@PathVariable final Long assetId, @RequestParam("data") String data) {
		final Asset asset = assetFacade.excludeMarketPrice(assetId, data);
		return ResponseEntity.ok().body(modelMapper.map(asset, AssetDTO.class));
	}
	
	/**
	 * Endpoint REST para cadastro de um novo movimento
	 * 
	 * @param assetDTO
	 * @param assetMovementDTO
	 * @return AssetDTO
	 */
	@PostMapping("/{assetId}/movimentacao")
	public ResponseEntity<AssetDTO> includeMovement(@PathVariable final Long assetId, @RequestBody final AssetMovementDTO assetMovementDTO) {
		//TODO: Fazer mapeamento devido com Account
		final Asset asset = assetFacade.includeMovement(1L, assetId, modelMapper.map(assetMovementDTO, AssetMovement.class));
		final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(asset.getId()).toUri();
		return ResponseEntity
				.created(uri)
				.body(modelMapper.map(asset, AssetDTO.class));
	}
	
	/**
	 * Endpoint REST para busca de movimentações de um ativo entre datas específicas
	 * 
	 * @param assetId
	 * @return AssetDTO
	 */
	@GetMapping("/{assetId}/movimentacao")
	public ResponseEntity<List<AssetMovementDTO>> findMovementsByDate(@PathVariable final Long assetId, 
			@RequestParam("dataInicio") String dataInicio, @RequestParam("dataFim") String dataFim) {
		final List<AssetMovement> movements = assetFacade.getMovements(assetId, dataInicio, dataFim);
		return ResponseEntity.ok()
				.body(movements.stream()
						.map(movement -> modelMapper.map(movement, AssetMovementDTO.class))
						.collect(Collectors.toList()));
	}
	
	private List<PositionDTO> createPositions(List<Asset> assets, String data) {
		LocalDate date = LocalDate.parse(data, formatter);
		List<PositionDTO> positions = new ArrayList<>();
		for (Asset asset : assets) {
			positions.add(PositionDTO.builder()
					.nomeAtivo(asset.getName())
					.tipoAtivo(asset.getType())
					.quantidadeTotal(asset.getTotalQuantity(date))
					.valorMercadoTotal(asset.getTotalMarketPrice(date))
					.rendimento(asset.getIncome(date))
					.lucro(asset.getProfit(date))
					.build());
		}
		return positions;
	}

}
