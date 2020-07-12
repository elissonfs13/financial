package com.maps.financial.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.maps.financial.domain.asset.AssetType;
import com.maps.financial.domain.asset.MovementType;
import com.maps.financial.resources.asset.dto.AssetDTO;
import com.maps.financial.resources.asset.dto.AssetMovementDTO;
import com.maps.financial.resources.asset.dto.MarketPriceDTO;
import com.maps.financial.resources.asset.dto.PositionDTO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AssetIntegrationTest extends AbstractIntegrationTest {
	
	private static final String BASE_URL = "/ativo";
	private static final String BASE_URL_ID = BASE_URL + "/{0}";
	private static final String POSITION_ID = BASE_URL + "/posicao";
	private static final String MOVEMENT_URL = BASE_URL_ID + "/movimentacao";
	private static final String ADD_MARKET_PRICE_URL = BASE_URL_ID + "/adiciona-valor-mercado";
	private static final String DELETE_MARKET_PRICE_URL = BASE_URL_ID + "/exclui-valor-mercado";
	
	private static final String nomeAtivoUpdated = "ATIVO 002";
	private static Long assetId;
	
	@Test
	public void stage001_createNewAssetTest() throws Exception {
		final AssetDTO asset = createAsset();
        final AssetDTO result = postForObject(BASE_URL, asset, CREATED, AssetDTO.class);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(AssetType.RF, result.getType());
        assetId = result.getId();
	}
	
	@Test
	public void stage002_findByIdTest() throws Exception {
		final AssetDTO asset = getForObject(BASE_URL_ID, OK, AssetDTO.class, assetId);
		assertNotNull(asset);
		assertEquals(assetId, asset.getId());
	}
	
	@Test
	public void stage003_updateTest() throws Exception {
		final AssetDTO asset = createAsset();
		asset.setName(nomeAtivoUpdated);
		asset.setId(assetId);

		final AssetDTO assetDTO = putForObject(BASE_URL_ID, asset, OK, AssetDTO.class, assetId);
		assertNotNull(assetDTO);
		assertEquals(nomeAtivoUpdated, assetDTO.getName());
	}
	
	@Test
	public void stage004_includeMovement1() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 7, 9));
		final AssetDTO result = postForObject(MOVEMENT_URL, movement, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMovements());
        assertEquals(1, result.getMovements().size());
        assertEquals(movement.getDate(), result.getMovements().get(0).getDate());
	}
	
	@Test
	public void stage005_includeMovement2() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 7, 10));
		final AssetDTO result = postForObject(MOVEMENT_URL, movement, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMovements());
        assertEquals(2, result.getMovements().size());
        assertEquals(movement.getDate(), result.getMovements().get(1).getDate());
	}
	
	@Test
	public void stage006_includeMovement3() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 7, 15));
		final AssetDTO result = postForObject(MOVEMENT_URL, movement, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMovements());
        assertEquals(3, result.getMovements().size());
        assertEquals(movement.getDate(), result.getMovements().get(2).getDate());
	}
	
	@Test
	public void stage007_includeMovement4() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 7, 20));
		final AssetDTO result = postForObject(MOVEMENT_URL, movement, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMovements());
        assertEquals(4, result.getMovements().size());
        assertEquals(movement.getDate(), result.getMovements().get(3).getDate());
	}
	
	@Test
	public void stage008_includeMovement5() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 7, 21));
		final AssetDTO result = postForObject(MOVEMENT_URL, movement, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMovements());
        assertEquals(5, result.getMovements().size());
        assertEquals(movement.getDate(), result.getMovements().get(4).getDate());
	}
	
	@Test
	public void stage009_findMovementsByDateTest() throws Exception {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("dataInicio", "2020-07-10");
		params.add("dataFim", "2020-07-20");
		final List<AssetMovementDTO> movements = Arrays.asList(getForList(MOVEMENT_URL, OK, AssetMovementDTO[].class, params, assetId));
		assertNotNull(movements);
		assertEquals(3, movements.size());
	}
	
	@Test
	public void stage010_includeMarketPrice1() throws Exception {
		final MarketPriceDTO marketPrice = createMarketPrice();
		marketPrice.setDate(LocalDate.of(2020, 7, 2));
		final AssetDTO result = postForObject(ADD_MARKET_PRICE_URL, marketPrice, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMarketPrices());
        assertEquals(1, result.getMarketPrices().size());
        assertEquals(marketPrice.getDate(), result.getMarketPrices().get(0).getDate());
	}
	
	@Test
	public void stage011_includeMarketPrice2() throws Exception {
		final MarketPriceDTO marketPrice = createMarketPrice();
		marketPrice.setDate(LocalDate.of(2020, 7, 5));
		final AssetDTO result = postForObject(ADD_MARKET_PRICE_URL, marketPrice, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMarketPrices());
        assertEquals(2, result.getMarketPrices().size());
        assertEquals(marketPrice.getDate(), result.getMarketPrices().get(1).getDate());
	}
	
	@Test
	public void stage012_excludeMarketPrice() throws Exception {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("data", "2020-07-02");
		final AssetDTO result = putForObject(DELETE_MARKET_PRICE_URL, OK, AssetDTO.class, params, assetId);
		assertNotNull(result);
		assertEquals(1, result.getMarketPrices().size());
        assertEquals(LocalDate.of(2020, 7, 5), result.getMarketPrices().get(0).getDate());
	}
	
	@Test
	public void stage013_findAssetsPositionTest() throws Exception {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("data", "2020-07-15");
		final List<PositionDTO> positions = Arrays.asList(getForList(POSITION_ID, OK, PositionDTO[].class, params, assetId));
		assertNotNull(positions);
		assertEquals(1, positions.size());
		assertEquals(nomeAtivoUpdated, positions.get(0).getNomeAtivo());
		assertEquals(formatBigDecimalScale(3.00), positions.get(0).getQuantidadeTotal());
		assertEquals(formatBigDecimalScale(10.50), positions.get(0).getValorMercadoTotal());
		assertEquals(formatBigDecimalScale(0.23), positions.get(0).getRendimento());
		assertEquals(formatBigDecimalScale(-45.00), positions.get(0).getLucro());
	}

	@Test
	public void stage014_deleteTest() throws Exception {
		deleteObject(BASE_URL_ID, OK, AssetDTO.class, assetId);
	}

	private BigDecimal formatBigDecimalScale(Double returnedValue) {
		BigDecimal bigDecimalFormated = new BigDecimal(returnedValue);
		return bigDecimalFormated.setScale(2, BigDecimal.ROUND_HALF_EVEN);
	}

	private AssetDTO createAsset() {
		return AssetDTO.builder()
				.name("ATIVO 001")
				.type(AssetType.RF)
				.issueDate(LocalDate.of(2020, 7, 1))
				.dueDate(LocalDate.of(2020, 7, 31))
				.build();
	}
	
	private AssetMovementDTO createMovement() {
		return AssetMovementDTO.builder()
				.value(new BigDecimal(15.00))
				.quantity(new BigDecimal(1))
				.type(MovementType.BUY)
				.build();
	}
	
	private MarketPriceDTO createMarketPrice() {
		return MarketPriceDTO.builder()
				.price(new BigDecimal(3.50))
				.build();
	}
	

}
