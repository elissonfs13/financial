package com.maps.financial.domain.asset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.maps.financial.exceptions.AssetQuantityNotAvailable;
import com.maps.financial.exceptions.AuthorizationException;
import com.maps.financial.exceptions.IssueDateNotBeforeDueDate;
import com.maps.financial.exceptions.MovementNotAllowedInDate;
import com.maps.financial.exceptions.ObjectNotFoundException;
import com.maps.financial.infra.security.SecurityUtils;

@RunWith(MockitoJUnitRunner.class)
public class AssetServiceTest {
	
	@Mock
	private AssetRepository repository;
	
	@Mock
	private SecurityUtils securityUtils;
	
	@InjectMocks
	private AssetService service;
	
	private static final Long ASSET_ID = 1L;
	private static final String NAME_ORIGINAL = "NAME_ORIGINAL";
	private static final String NAME_UPDATED = "NAME_UPDATED";
	private static final LocalDate DATA_FIRST_MARKET_PRICE = LocalDate.of(2020, 6, 21);
	private static final BigDecimal VALUE_FIRST_MARKET_PRICE = new BigDecimal(1.00);
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private Asset asset;
	private List<Asset> assets;
	
	@Before
	public void init() {
		asset = createAsset();
		asset.includeMarketPrice(VALUE_FIRST_MARKET_PRICE, DATA_FIRST_MARKET_PRICE);
		assets = new ArrayList<>();
		assets.add(asset);
	}

	@Test
	public void findByIdTest(){
		final Optional<Asset> optional = Optional.of(asset);	
		when(repository.findById(ASSET_ID)).thenReturn(optional);	
		final Asset assetReturned = service.findById(ASSET_ID);
		assertNotNull(assetReturned);
		assertEquals(asset, assetReturned);
		verify(repository, times(1)).findById(ASSET_ID);
	}
	
	@Test(expected = ObjectNotFoundException.class)
	public void findByIdWithObjectNotFoundExceptionTest(){
		service.findById(2L);
	}
	
	@Test
	public void findAllTest() {
		when(repository.findAll()).thenReturn(assets);
		final List<Asset> listAssets = service.findAll();			
		assertEquals(1, listAssets.size());	
		verify(repository, times(1)).findAll();
	}
	
	@Test
	public void createTest() {
		when(repository.save(asset)).thenReturn(asset);
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.TRUE);
		final Asset assetReturned = service.create(asset);			
		assertEquals(asset, assetReturned);	
		verify(repository, times(1)).save(asset);
	}
	
	@Test(expected = AuthorizationException.class)
	public void createWithUserNotAdminTest() {
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.FALSE);
		final Asset assetReturned = service.create(asset);			
		assertEquals(asset, assetReturned);	
		verify(repository, times(1)).save(asset);
	}
	
	@Test(expected = IssueDateNotBeforeDueDate.class)
	public void createWithoutIssueDateTest() {
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.TRUE);
		Asset asset = Asset.builder()
				 .id(ASSET_ID)
				 .type(AssetType.RF)
				 .name(NAME_ORIGINAL)
				 .dueDate(LocalDate.of(2020, 8, 10))
				 .build();

		service.create(asset);			
	}
	
	@Test(expected = IssueDateNotBeforeDueDate.class)
	public void createWithoutDueDateTest() {
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.TRUE);
		Asset asset = Asset.builder()
				 .id(ASSET_ID)
				 .type(AssetType.RF)
				 .name(NAME_ORIGINAL)
				 .issueDate(LocalDate.of(2020, 6, 10))
				 .build();

		service.create(asset);			
	}
	
	@Test(expected = IssueDateNotBeforeDueDate.class)
	public void createWithIssueDateGreaterThanDueDateTest() {
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.TRUE);
		Asset asset = Asset.builder()
				 .id(ASSET_ID)
				 .type(AssetType.RF)
				 .name(NAME_ORIGINAL)
				 .issueDate(LocalDate.of(2020, 9, 10))
				 .dueDate(LocalDate.of(2020, 8, 10))
				 .build();
		
		service.create(asset);			
	}
	
	@Test
	public void updateTest() {		
		final Optional<Asset> optional = Optional.of(asset);	
		when(repository.findById(ASSET_ID)).thenReturn(optional);	
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.TRUE);
		
		final Asset assetUpdate = Asset.builder()
				.id(100L)
				.name(NAME_UPDATED)
				.build();
		
		final Asset assetReturned = service.update(ASSET_ID, assetUpdate);	
		assertNotNull(assetReturned);		
		assertEquals(assetUpdate.getName(), assetReturned.getName());
		assertNotEquals(assetUpdate.getId(), assetReturned.getId());
	}
	
	@Test(expected = AuthorizationException.class)
	public void updateWithUserNotAdminTest() {		
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.FALSE);
		
		final Asset assetUpdate = Asset.builder()
				.id(100L)
				.name(NAME_UPDATED)
				.build();
		
		service.update(ASSET_ID, assetUpdate);	
	}
	
	@Test
	public void deleteTest() {
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.TRUE);
		service.delete(ASSET_ID);		
		verify(repository, times(1)).delete(asset);
	}
	
	@Test(expected = AuthorizationException.class)
	public void deleteWithUserNotAdminTest() {
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.FALSE);
		service.delete(ASSET_ID);		
	}
	
	@Test
	public void includeMovementBuyTest() {
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.FALSE);
		AssetMovement movement = createAssetMovement(1L, MovementType.BUY, 1.00, 1.00, LocalDate.of(2020, 7, 10));
		Asset assetReturned = service.includeMovementByAssetId(ASSET_ID, movement);
		assertNotNull(assetReturned);
		assertEquals(1, assetReturned.getMovements().size());	
		assertEquals(movement, assetReturned.getMovements().get(0));	
	}
	
	@Test(expected = AssetQuantityNotAvailable.class)
	public void includeMovementSellWithoutQuantityTest() {
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.FALSE);
		AssetMovement movement = createAssetMovement(1L, MovementType.SELL, 1.00, 1.00, LocalDate.of(2020, 7, 10));
		service.includeMovementByAssetId(ASSET_ID, movement);
	}
	
	@Test(expected = AuthorizationException.class)
	public void includeMovementWithUserAdminTest() {
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.TRUE);
		AssetMovement movement = createAssetMovement(1L, MovementType.SELL, 1.00, 1.00, LocalDate.of(2020, 7, 10));
		service.includeMovementByAssetId(ASSET_ID, movement);
	}
	
	@Test
	public void includeMovementSellWithQuantityTest() {
		asset.includeMovement(createAssetMovement(1L, MovementType.BUY, 4.00, 4.00, LocalDate.of(2020, 7, 10)));
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.FALSE);
		AssetMovement movement = createAssetMovement(2L, MovementType.SELL, 1.00, 1.00, LocalDate.of(2020, 7, 10));
		Asset assetReturned = service.includeMovementByAssetId(ASSET_ID, movement);
		assertNotNull(assetReturned);
		assertEquals(2, assetReturned.getMovements().size());	
		assertEquals(movement, assetReturned.getMovements().get(1));
	}
	
	@Test(expected = MovementNotAllowedInDate.class)
	public void includeMovementWithDateBeforeIssueDate() {
		asset.includeMovement(createAssetMovement(1L, MovementType.BUY, 4.00, 4.00, LocalDate.of(2020, 7, 10)));
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.FALSE);
		AssetMovement movement = createAssetMovement(2L, MovementType.SELL, 1.00, 1.00, LocalDate.of(2020, 2, 28));
		service.includeMovementByAssetId(ASSET_ID, movement);
	}
	
	@Test(expected = MovementNotAllowedInDate.class)
	public void includeMovementWithDateAfterDueDate() {
		asset.includeMovement(createAssetMovement(1L, MovementType.BUY, 4.00, 4.00, LocalDate.of(2020, 7, 10)));
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.FALSE);
		AssetMovement movement = createAssetMovement(2L, MovementType.SELL, 1.00, 1.00, LocalDate.of(2021, 2, 28));
		service.includeMovementByAssetId(ASSET_ID, movement);
	}
	
	@Test(expected = MovementNotAllowedInDate.class)
	public void includeMovementWithDateEqualDueDate() {
		asset.includeMovement(createAssetMovement(1L, MovementType.BUY, 4.00, 4.00, LocalDate.of(2020, 7, 10)));
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.FALSE);
		AssetMovement movement = createAssetMovement(2L, MovementType.SELL, 1.00, 1.00, asset.getDueDate());
		service.includeMovementByAssetId(ASSET_ID, movement);
	}
	
	@Test(expected = MovementNotAllowedInDate.class)
	public void includeMovementInWeekend() {
		asset.includeMovement(createAssetMovement(1L, MovementType.BUY, 4.00, 4.00, LocalDate.of(2020, 7, 10)));
		when(securityUtils.currentUserIsAdmin()).thenReturn(Boolean.FALSE);
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		AssetMovement movement = createAssetMovement(2L, MovementType.SELL, 1.00, 1.00, LocalDate.of(2020, 7, 11)); // 11/07/2020 é sábado
		service.includeMovementByAssetId(ASSET_ID, movement);
	}
	
	@Test
	public void includeMarketPriceAfterTest() {
		BigDecimal priceMarketPrice = new BigDecimal(10.00);
		LocalDate dateMarketPrice = LocalDate.of(2020, 6, 22);
		asset.includeMovement(createAssetMovement(1L, MovementType.BUY, 4.00, 4.00, LocalDate.of(2020, 7, 10)));
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		assertEquals(1, asset.getMarketPrices().size());
		Asset assetReturned = service.includeMarketPrice(ASSET_ID, priceMarketPrice, dateMarketPrice);
		assertNotNull(assetReturned);
		assertEquals(2, assetReturned.getMarketPrices().size());	
		assertEquals(VALUE_FIRST_MARKET_PRICE.setScale(8, BigDecimal.ROUND_DOWN), assetReturned.getMarketPrices().get(0).getPrice());
		assertEquals(priceMarketPrice.setScale(8, BigDecimal.ROUND_DOWN), assetReturned.getMarketPrices().get(1).getPrice());
		assertEquals(formatBigDecimalScale(40.00), assetReturned.getTotalMarketPrice(LocalDate.of(2020, 7, 10)));
	}
	
	@Test
	public void includeMarketPriceBeforeTest() {
		BigDecimal priceMarketPrice = new BigDecimal(10.00);
		LocalDate dateMarketPrice = LocalDate.of(2020, 6, 20);
		asset.includeMovement(createAssetMovement(1L, MovementType.BUY, 4.00, 4.00, LocalDate.of(2020, 7, 10)));
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		assertEquals(1, asset.getMarketPrices().size());
		Asset assetReturned = service.includeMarketPrice(ASSET_ID, priceMarketPrice, dateMarketPrice);
		assertNotNull(assetReturned);
		assertEquals(2, assetReturned.getMarketPrices().size());	
		assertEquals(VALUE_FIRST_MARKET_PRICE.setScale(8, BigDecimal.ROUND_DOWN), assetReturned.getMarketPrices().get(0).getPrice());
		assertEquals(priceMarketPrice.setScale(8, BigDecimal.ROUND_DOWN), assetReturned.getMarketPrices().get(1).getPrice());
		assertEquals(formatBigDecimalScale(4.00), assetReturned.getTotalMarketPrice(LocalDate.of(2020, 7, 10)));
	}
	
	@Test
	public void includeMarketPriceWithoutDateTest() {
		BigDecimal priceMarketPrice = new BigDecimal(10.00);
		asset.includeMovement(createAssetMovement(1L, MovementType.BUY, 4.00, 4.00, LocalDate.of(2020, 7, 10)));
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		assertEquals(1, asset.getMarketPrices().size());
		Asset assetReturned = service.includeMarketPrice(ASSET_ID, priceMarketPrice, null);
		assertNotNull(assetReturned);
		assertEquals(1, assetReturned.getMarketPrices().size());	
		assertEquals(VALUE_FIRST_MARKET_PRICE.setScale(8, BigDecimal.ROUND_DOWN), assetReturned.getMarketPrices().get(0).getPrice());
		assertEquals(formatBigDecimalScale(4.00), assetReturned.getTotalMarketPrice(LocalDate.of(2020, 7, 10)));
	}
	
	@Test
	public void includeMarketPriceWithoutValueTest() {
		LocalDate dateMarketPrice = LocalDate.of(2020, 6, 20);
		asset.includeMovement(createAssetMovement(1L, MovementType.BUY, 4.00, 4.00, LocalDate.of(2020, 7, 10)));
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		assertEquals(1, asset.getMarketPrices().size());
		Asset assetReturned = service.includeMarketPrice(ASSET_ID, null, dateMarketPrice);
		assertNotNull(assetReturned);
		assertEquals(1, assetReturned.getMarketPrices().size());	
		assertEquals(VALUE_FIRST_MARKET_PRICE.setScale(8, BigDecimal.ROUND_DOWN), assetReturned.getMarketPrices().get(0).getPrice());
		assertEquals(formatBigDecimalScale(4.00), assetReturned.getTotalMarketPrice(LocalDate.of(2020, 7, 10)));
	}
	
	@Test
	public void excludeUniqueMarketPriceTest() {
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		assertEquals(1, asset.getMarketPrices().size());
		Asset assetReturned = service.excludeMarketPrice(ASSET_ID, DATA_FIRST_MARKET_PRICE.format(formatter));
		assertNotNull(assetReturned);
		assertEquals(0, assetReturned.getMarketPrices().size());
		assertEquals(formatBigDecimalScale(0.00), assetReturned.getTotalMarketPrice(LocalDate.of(2020, 7, 10)));
	}
	
	@Test
	public void excludeMarketPriceTest() {
		BigDecimal priceMarketPrice = new BigDecimal(10.00);
		LocalDate dateMarketPrice = LocalDate.of(2020, 6, 20);
		asset.includeMarketPrice(priceMarketPrice, dateMarketPrice);
		asset.includeMovement(createAssetMovement(1L, MovementType.BUY, 4.00, 4.00, LocalDate.of(2020, 7, 10)));
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		assertEquals(2, asset.getMarketPrices().size());
		Asset assetReturned = service.excludeMarketPrice(ASSET_ID, DATA_FIRST_MARKET_PRICE.format(formatter));
		assertNotNull(assetReturned);
		assertEquals(1, assetReturned.getMarketPrices().size());
		assertEquals(priceMarketPrice.setScale(8, BigDecimal.ROUND_DOWN), assetReturned.getMarketPrices().get(0).getPrice());
		assertEquals(formatBigDecimalScale(40.00), assetReturned.getTotalMarketPrice(LocalDate.of(2020, 7, 10)));
	}
	
	@Test
	public void getTotalQuantityTest() {
		addMovements();
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		BigDecimal valueReturned = service.getTotalQuantity(ASSET_ID, LocalDate.of(2020, 7, 10));
		assertNotNull(valueReturned);
		assertEquals(formatBigDecimalScale(4.81), valueReturned);	
	}
	
	@Test
	public void getTotalQuantityWithoutDateTest() {
		addMovements();
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		BigDecimal valueReturned = service.getTotalQuantity(ASSET_ID, null);
		assertNotNull(valueReturned);
		assertEquals(formatBigDecimalScale(0.00), valueReturned);	
	}
	
	@Test
	public void getTotalMarketPriceTest() {
		addMovements();
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		BigDecimal valueReturned = service.getTotalMarketPrice(ASSET_ID, LocalDate.of(2020, 7, 10));
		assertNotNull(valueReturned);
		assertEquals(formatBigDecimalScale(4.81), valueReturned);	
	}
	
	@Test
	public void getTotalMarketPriceWithoutDateTest() {
		addMovements();
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		BigDecimal valueReturned = service.getTotalMarketPrice(ASSET_ID, null);
		assertNotNull(valueReturned);
		assertEquals(formatBigDecimalScale(0.00), valueReturned);	
	}
	
	@Test
	public void getIncomeTest() {
		addMovements();
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		BigDecimal valueReturned = service.getIncome(ASSET_ID, LocalDate.of(2020, 7, 10));
		assertNotNull(valueReturned);
		assertEquals(formatBigDecimalScale(1.04), valueReturned);	
	}
	
	@Test
	public void getIncomeWithoutMovementsTest() {
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		BigDecimal valueReturned = service.getIncome(ASSET_ID, LocalDate.of(2020, 7, 10));
		assertNotNull(valueReturned);
		assertEquals(formatBigDecimalScale(0.00), valueReturned);	
	}
	
	@Test
	public void getIncomeWithoutDateTest() {
		addMovements();
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		BigDecimal valueReturned = service.getIncome(ASSET_ID, null);
		assertNotNull(valueReturned);
		assertEquals(formatBigDecimalScale(0.00), valueReturned);	
	}
	
	@Test
	public void getProfitTest() {
		addMovements();
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		BigDecimal valueReturned = service.getProfit(ASSET_ID, LocalDate.of(2020, 7, 10));
		assertNotNull(valueReturned);
		assertEquals(formatBigDecimalScale(4.90), valueReturned);	
	}
	
	@Test
	public void getProfitWithoutDateTest() {
		addMovements();
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		BigDecimal valueReturned = service.getProfit(ASSET_ID, null);
		assertNotNull(valueReturned);
		assertEquals(formatBigDecimalScale(0.00), valueReturned);	
	}
	
	@Test
	public void getMovementsTest() {
		addMovements();
		String dateBegin = LocalDate.of(2020, 7, 8).format(formatter);
		String dateEnd = LocalDate.of(2020, 7, 10).format(formatter);
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		List<AssetMovement> movementsReturned = service.getMovements(ASSET_ID, dateBegin, dateEnd);
		assertNotNull(movementsReturned);
		assertEquals(3, movementsReturned.size());	
		assertEquals(formatBigDecimalScale(5.15), movementsReturned.get(0).getValue());
		assertEquals(formatBigDecimalScale(3.60), movementsReturned.get(2).getValue());
		
	}
	
	private BigDecimal formatBigDecimalScale(Double returnedValue) {
		BigDecimal bigDecimalFormated = new BigDecimal(returnedValue);
		return bigDecimalFormated.setScale(2, BigDecimal.ROUND_HALF_EVEN);
	}
	
	private void addMovements() {
		asset.includeMovement(createAssetMovement(1L, MovementType.BUY, 4.50, 4.20, LocalDate.of(2020, 7, 7)));
		asset.includeMovement(createAssetMovement(2L, MovementType.BUY, 5.20, 5.15, LocalDate.of(2020, 7, 8)));
		asset.includeMovement(createAssetMovement(3L, MovementType.SELL, 1.75, 10.65, LocalDate.of(2020, 7, 9)));
		asset.includeMovement(createAssetMovement(4L, MovementType.SELL, 3.15, 3.60, LocalDate.of(2020, 7, 10)));
		
		//Os seguintes movimentos adicionados não fazem diferença nos cálculos pois estão com data posterior a data pesquisada
		asset.includeMovement(createAssetMovement(2L, MovementType.BUY, 1.20, 1.15, LocalDate.of(2020, 7, 20)));
		asset.includeMovement(createAssetMovement(3L, MovementType.SELL, 1.75, 1.65, LocalDate.of(2020, 7, 20)));
	}
	
	private Asset createAsset() {
		return Asset.builder()
				.id(ASSET_ID)
				.type(AssetType.RF)
				.name(NAME_ORIGINAL)
				.issueDate(LocalDate.of(2020, 6, 10))
				.dueDate(LocalDate.of(2020, 8, 10))
				.build();
	}
	
	private AssetMovement createAssetMovement(Long id, MovementType type, Double quantity, Double value, LocalDate date) {
		return AssetMovement.builder()
				.id(id)
				.type(type)
				.quantity(new BigDecimal(quantity))
				.value(new BigDecimal(value))
				.date(date)
				.build();
	}
	

}
