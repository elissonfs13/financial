package com.maps.financial.domain.asset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
import com.maps.financial.exceptions.ObjectNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class AssetServiceTest {
	
	@Mock
	private AssetRepository repository;
	
	@InjectMocks
	private AssetService service;
	
	private static final Long ASSET_ID = 1L;
	private static final String NAME_ORIGINAL = "NAME_ORIGINAL";
	private static final String NAME_UPDATED = "NAME_UPDATED";
	private Asset asset;
	private List<Asset> assets;
	
	@Before
	public void init() {
		asset = createAsset();
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
		final Asset assetReturned = service.create(asset);			
		assertEquals(asset, assetReturned);	
		verify(repository, times(1)).save(asset);
	}
	
	@Test
	public void updateTest() {		
		final Optional<Asset> optional = Optional.of(asset);	
		when(repository.findById(ASSET_ID)).thenReturn(optional);	
		
		final Asset assetUpdate = Asset.builder()
				.id(100L)
				.name(NAME_UPDATED)
				.build();
		
		final Asset assetReturned = service.update(ASSET_ID, assetUpdate);	
		assertNotNull(assetReturned);		
		assertEquals(assetUpdate.getName(), assetReturned.getName());
		assertNotEquals(assetUpdate.getId(), assetReturned.getId());
	}
	
	@Test
	public void deleteTest() {
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		service.delete(ASSET_ID);		
		verify(repository, times(1)).delete(asset);
	}
	
	@Test
	public void includeMovementBuyTest() {
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		AssetMovement movement = createAssetMovement(1L, MovementType.BUY, 1.00, 1.00);
		Asset assetReturned = service.includeMovement(ASSET_ID, movement);
		assertNotNull(assetReturned);
		assertEquals(1, assetReturned.getMovements().size());	
		assertEquals(movement, assetReturned.getMovements().get(0));	
	}
	
	@Test(expected = AssetQuantityNotAvailable.class)
	public void includeMovementSellWithoutQuantityTest() {
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		AssetMovement movement = createAssetMovement(1L, MovementType.SELL, 1.00, 1.00);
		service.includeMovement(ASSET_ID, movement);
	}
	
	@Test
	public void includeMovementSellWithQuantityTest() {
		asset.includeMovement(createAssetMovement(1L, MovementType.BUY, 4.00, 4.00));
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		AssetMovement movement = createAssetMovement(2L, MovementType.SELL, 1.00, 1.00);
		Asset assetReturned = service.includeMovement(ASSET_ID, movement);
		assertNotNull(assetReturned);
		assertEquals(2, assetReturned.getMovements().size());	
		assertEquals(movement, assetReturned.getMovements().get(1));
	}
	
	@Test
	public void getTotalQuantityTest() {
		addMovements();
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		BigDecimal valueReturned = service.getTotalQuantity(ASSET_ID, null);
		assertNotNull(valueReturned);
		assertEquals(formatBigDecimalScale(4.81), valueReturned);	
	}
	
	@Test
	public void getTotalMarketPriceTest() {
		addMovements();
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		BigDecimal valueReturned = service.getTotalMarketPrice(ASSET_ID, null);
		assertNotNull(valueReturned);
		assertEquals(formatBigDecimalScale(10.82), valueReturned);	
	}
	
	@Test
	public void getIncomeTest() {
		addMovements();
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		BigDecimal valueReturned = service.getIncome(ASSET_ID, null);
		assertNotNull(valueReturned);
		assertEquals(formatBigDecimalScale(2.34), valueReturned);	
	}
	
	@Test
	public void getProfitTest() {
		addMovements();
		final Optional<Asset> optional = Optional.of(asset);
		when(repository.findById(ASSET_ID)).thenReturn(optional);
		BigDecimal valueReturned = service.getProfit(ASSET_ID, null);
		assertNotNull(valueReturned);
		assertEquals(formatBigDecimalScale(4.90), valueReturned);	
	}
	
	private BigDecimal formatBigDecimalScale(Double returnedValue) {
		BigDecimal bigDecimalFormated = new BigDecimal(returnedValue);
		return bigDecimalFormated.setScale(2, BigDecimal.ROUND_HALF_EVEN);
	}
	
	private void addMovements() {
		asset.includeMovement(createAssetMovement(1L, MovementType.BUY, 4.50, 4.20));
		asset.includeMovement(createAssetMovement(2L, MovementType.BUY, 5.20, 5.15));
		asset.includeMovement(createAssetMovement(3L, MovementType.SELL, 1.75, 10.65));
		asset.includeMovement(createAssetMovement(4L, MovementType.SELL, 3.15, 3.60));
	}
	
	private Asset createAsset() {
		return Asset.builder()
				.id(ASSET_ID)
				.type(AssetType.RF)
				.name(NAME_ORIGINAL)
				.build();
	}
	
	private AssetMovement createAssetMovement(Long id, MovementType type, Double quantity, Double value) {
		return AssetMovement.builder()
				.id(id)
				.type(type)
				.quantity(new BigDecimal(quantity))
				.value(new BigDecimal(value))
				.build();
	}
	

}
