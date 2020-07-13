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
import com.maps.financial.resources.commons.dto.DefaultErrorDTO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AssetIntegrationTest extends AbstractIntegrationTest {
	
	private static final String BASE_URL = "/ativo";
	private static final String MOVEMENT_URL = "/movimentacao";
	private static final String MOVEMENT_BUY_URL = MOVEMENT_URL + "/compra";
	private static final String MOVEMENT_SELL_URL = MOVEMENT_URL + "/venda";
	private static final String BASE_URL_ID = BASE_URL + "/{0}";
	private static final String POSITION_ID = BASE_URL + "/posicao";
	private static final String ASSETMOVEMENT_URL = BASE_URL_ID + MOVEMENT_URL;
	private static final String ADD_MARKET_PRICE_URL = BASE_URL_ID + "/adiciona-valor-mercado";
	private static final String DELETE_MARKET_PRICE_URL = BASE_URL_ID + "/exclui-valor-mercado";
	
	private static final String nomeAtivoUpdated = "ATIVO 002";
	private static Long assetId;
	
	@Test
	public void stage001_createNewAssetByNotAdminTest() throws Exception {
		final AssetDTO asset = createAsset();
		DefaultErrorDTO error = postForObject(BASE_URL, TOKEN_USER, asset, FORBIDDEN, DefaultErrorDTO.class);
		assertNotNull(error);
	}
	
	@Test
	public void stage002_createNewAssetWithIssueDateGreaterThanDueDateTest() throws Exception {
		final AssetDTO asset = createAsset();
		asset.setIssueDate(LocalDate.of(2020, 8, 1));
		DefaultErrorDTO error = postForObject(BASE_URL, TOKEN_ADMIN, asset, NOT_ACCEPTABLE, DefaultErrorDTO.class);
		assertNotNull(error);
	}
	
	@Test
	public void stage003_createNewAssetTest() throws Exception {
		final AssetDTO asset = createAsset();
        final AssetDTO result = postForObject(BASE_URL, TOKEN_ADMIN, asset, CREATED, AssetDTO.class);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(AssetType.RF, result.getType());
        assetId = result.getId();
	}
	
	@Test
	public void stage004_findByIdNoContentTest() throws Exception {
		Long assedIdNoContent = 100000L;
		DefaultErrorDTO error = getForObject(BASE_URL_ID, TOKEN_ADMIN, NO_CONTENT, DefaultErrorDTO.class, assedIdNoContent);
		assertNotNull(error);
	}
	
	@Test
	public void stage005_findByIdTest() throws Exception {
		final AssetDTO asset = getForObject(BASE_URL_ID, TOKEN_ADMIN, OK, AssetDTO.class, assetId);
		assertNotNull(asset);
		assertEquals(assetId, asset.getId());
	}
	
	@Test
	public void stage006_updateByNotAdminTest() throws Exception {
		final AssetDTO asset = createAsset();
		asset.setName(nomeAtivoUpdated);
		asset.setId(assetId);

		final AssetDTO assetDTO = putForObject(BASE_URL_ID, TOKEN_USER, asset, FORBIDDEN, AssetDTO.class, assetId);
		assertNotNull(assetDTO);
	}
	
	@Test
	public void stage007_updateTest() throws Exception {
		final AssetDTO asset = createAsset();
		asset.setName(nomeAtivoUpdated);
		asset.setId(assetId);

		final AssetDTO assetDTO = putForObject(BASE_URL_ID, TOKEN_ADMIN, asset, OK, AssetDTO.class, assetId);
		assertNotNull(assetDTO);
		assertEquals(nomeAtivoUpdated, assetDTO.getName());
	}
	
	@Test
	public void stage008_includeMovement1Test() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 7, 9));
		final AssetDTO result = postForObject(ASSETMOVEMENT_URL, TOKEN_USER, movement, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMovements());
        assertEquals(1, result.getMovements().size());
        assertEquals(movement.getDate(), result.getMovements().get(0).getDate());
	}
	
	@Test
	public void stage009_includeMovement2Test() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 7, 10));
		final AssetDTO result = postForObject(ASSETMOVEMENT_URL, TOKEN_USER, movement, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMovements());
        assertEquals(2, result.getMovements().size());
        assertEquals(movement.getDate(), result.getMovements().get(1).getDate());
	}
	
	@Test
	public void stage010_includeMovement3Test() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 7, 15));
		final AssetDTO result = postForObject(ASSETMOVEMENT_URL, TOKEN_USER, movement, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMovements());
        assertEquals(3, result.getMovements().size());
        assertEquals(movement.getDate(), result.getMovements().get(2).getDate());
	}
	
	@Test
	public void stage011_includeMovement4Test() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 7, 20));
		final AssetDTO result = postForObject(ASSETMOVEMENT_URL, TOKEN_USER, movement, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMovements());
        assertEquals(4, result.getMovements().size());
        assertEquals(movement.getDate(), result.getMovements().get(3).getDate());
	}
	
	@Test
	public void stage012_includeMovement5Test() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 7, 21));
		final AssetDTO result = postForObject(ASSETMOVEMENT_URL, TOKEN_USER, movement, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMovements());
        assertEquals(5, result.getMovements().size());
        assertEquals(movement.getDate(), result.getMovements().get(4).getDate());
	}
	
	@Test
	public void stage013_includeMovementByAdminTest() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 7, 20));
		DefaultErrorDTO error = postForObject(ASSETMOVEMENT_URL, TOKEN_ADMIN, movement, FORBIDDEN, DefaultErrorDTO.class, assetId);
		assertNotNull(error);
	}
	
	@Test
	public void stage014_includeMovementInWeekendTest() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 7, 12));
		DefaultErrorDTO error = postForObject(ASSETMOVEMENT_URL, TOKEN_USER, movement, NOT_ACCEPTABLE, DefaultErrorDTO.class, assetId);
		assertNotNull(error);
	}
	
	@Test
	public void stage015_includeMovementBeforeIssueDateTest() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 6, 12));
		DefaultErrorDTO error = postForObject(ASSETMOVEMENT_URL, TOKEN_USER, movement, NOT_ACCEPTABLE, DefaultErrorDTO.class, assetId);
		assertNotNull(error);
	}
	
	@Test
	public void stage016_includeMovementAfterDueDateTest() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 8, 12));
		DefaultErrorDTO error = postForObject(ASSETMOVEMENT_URL, TOKEN_USER, movement, NOT_ACCEPTABLE, DefaultErrorDTO.class, assetId);
		assertNotNull(error);
	}
	
	@Test
	public void stage017_includeMovementAccountWithoutBalanceTest() throws Exception {
		final AssetMovementDTO movement = createMovement();
		final String userWithoutBalance = "usuario1:senha1";
		movement.setDate(LocalDate.of(2020, 7, 12));
		DefaultErrorDTO error = postForObject(ASSETMOVEMENT_URL, userWithoutBalance, movement, NOT_ACCEPTABLE, DefaultErrorDTO.class, assetId);
		assertNotNull(error);
	}
	
	@Test
	public void stage018_includeMovementByUserUnauthorizedTest() throws Exception {
		final AssetMovementDTO movement = createMovement();
		final String userWithoutBalance = "usuario1:senha6"; //Senha incorreta
		movement.setDate(LocalDate.of(2020, 7, 12));
		postForObject(ASSETMOVEMENT_URL, userWithoutBalance, movement, UNAUTHORIZED, DefaultErrorDTO.class, assetId);
	}
	
	@Test
	public void stage019_includeMovementAssetWithoutQuantityTest() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 7, 12));
		movement.setType(MovementType.SELL);
		movement.setQuantity(new BigDecimal(10000));
		DefaultErrorDTO error = postForObject(ASSETMOVEMENT_URL, TOKEN_USER, movement, NOT_ACCEPTABLE, DefaultErrorDTO.class, assetId);
		assertNotNull(error);
	}
	
	@Test
	public void stage020_includeMovementBuyWithAssetNameTest() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 7, 13));
		movement.setType(null);
		movement.setAtivo(nomeAtivoUpdated);
		final AssetDTO result = postForObject(MOVEMENT_BUY_URL, TOKEN_USER, movement, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMovements());
        assertEquals(6, result.getMovements().size());
        assertEquals(movement.getDate(), result.getMovements().get(5).getDate());
	}
	
	@Test
	public void stage021_includeMovementSellWithAssetNameTest() throws Exception {
		final AssetMovementDTO movement = createMovement();
		movement.setDate(LocalDate.of(2020, 7, 14));
		movement.setType(null);
		movement.setAtivo(nomeAtivoUpdated);
		final AssetDTO result = postForObject(MOVEMENT_SELL_URL, TOKEN_USER, movement, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMovements());
        assertEquals(7, result.getMovements().size());
        assertEquals(movement.getDate(), result.getMovements().get(6).getDate());
	}
	
	@Test
	public void stage022_findMovementsByDateTest() throws Exception {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("dataInicio", "2020-07-10");
		params.add("dataFim", "2020-07-20");
		final List<AssetMovementDTO> movements = Arrays.asList(getForList(ASSETMOVEMENT_URL, TOKEN_ADMIN, OK, AssetMovementDTO[].class, params, assetId));
		assertNotNull(movements);
		assertEquals(5, movements.size());
	}
	
	@Test
	public void stage023_includeMarketPrice1Test() throws Exception {
		final MarketPriceDTO marketPrice = createMarketPrice();
		marketPrice.setDate(LocalDate.of(2020, 7, 2));
		final AssetDTO result = postForObject(ADD_MARKET_PRICE_URL, TOKEN_ADMIN, marketPrice, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMarketPrices());
        assertEquals(1, result.getMarketPrices().size());
        assertEquals(marketPrice.getDate(), result.getMarketPrices().get(0).getDate());
	}
	
	@Test
	public void stage024_includeMarketPrice2Test() throws Exception {
		final MarketPriceDTO marketPrice = createMarketPrice();
		marketPrice.setDate(LocalDate.of(2020, 7, 5));
		final AssetDTO result = postForObject(ADD_MARKET_PRICE_URL, TOKEN_ADMIN, marketPrice, CREATED, AssetDTO.class, assetId);
		assertNotNull(result);
        assertNotNull(result.getMarketPrices());
        assertEquals(2, result.getMarketPrices().size());
        assertEquals(marketPrice.getDate(), result.getMarketPrices().get(1).getDate());
	}
	
	@Test
	public void stage025_excludeMarketPriceTest() throws Exception {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("data", "2020-07-02");
		final AssetDTO result = putForObject(DELETE_MARKET_PRICE_URL, TOKEN_ADMIN, OK, AssetDTO.class, params, assetId);
		assertNotNull(result);
		assertEquals(1, result.getMarketPrices().size());
        assertEquals(LocalDate.of(2020, 7, 5), result.getMarketPrices().get(0).getDate());
	}
	
	@Test
	public void stage026_findAssetsPositionTest() throws Exception {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("data", "2020-07-15");
		final List<PositionDTO> positions = Arrays.asList(getForList(POSITION_ID, TOKEN_ADMIN, OK, PositionDTO[].class, params, assetId));
		assertNotNull(positions);
		assertEquals(129, positions.size());
		assertEquals(nomeAtivoUpdated, positions.get(128).getNomeAtivo());
		assertEquals(formatBigDecimalScale(3.00), positions.get(128).getQuantidadeTotal());
		assertEquals(formatBigDecimalScale(10.50), positions.get(128).getValorMercadoTotal());
		assertEquals(formatBigDecimalScale(0.23), positions.get(128).getRendimento());
		assertEquals(formatBigDecimalScale(-45.00), positions.get(128).getLucro());
	}
	
	@Test
	public void stage027_deleteByNotAdminTest() throws Exception {
		DefaultErrorDTO error = deleteObject(BASE_URL_ID, TOKEN_USER, FORBIDDEN, DefaultErrorDTO.class, assetId);
		assertNotNull(error);
	}

	@Test
	public void stage028_deleteTest() throws Exception {
		deleteObject(BASE_URL_ID, TOKEN_ADMIN, OK, AssetDTO.class, assetId);
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
