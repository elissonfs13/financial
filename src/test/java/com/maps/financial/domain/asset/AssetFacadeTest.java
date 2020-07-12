package com.maps.financial.domain.asset;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.maps.financial.domain.account.AccountFacade;

@RunWith(MockitoJUnitRunner.class)
public class AssetFacadeTest {
	
	@InjectMocks
	private AssetFacade facade;
	
	@Mock
	private AssetService service;
	
	@Mock
	private AccountFacade accountFacade;
	
	private static final Long ASSET_ID = 1L;
	private static final Long ACCOUNT_ID = 2L;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private Asset asset;
	
	@Before
	public void init() {
		asset = createAsset();
	}
	
	@Test
	public void findByIdTest() {
		facade.findById(ASSET_ID);
		verify(service, times(1)).findById(ASSET_ID);
	}
	
	@Test
	public void findAllTest() {
		facade.findAll();
		verify(service, times(1)).findAll();
	}
	
	@Test
	public void createTest() {
		facade.create(asset);
		verify(service, times(1)).create(asset);
	}
	
	@Test
	public void updateTest() {
		facade.update(ASSET_ID, asset);
		verify(service, times(1)).update(ASSET_ID, asset);
	}
	
	@Test
	public void deleteTest() {
		facade.delete(ASSET_ID);
		verify(service, times(1)).delete(ASSET_ID);
	}
	
	@Test
	public void getTotalQuantityTest() {
		LocalDate date = LocalDate.now();
		facade.getTotalQuantity(ASSET_ID, date);
		verify(service, times(1)).getTotalQuantity(ASSET_ID, date);
	}
	
	@Test
	public void getTotalMarketPriceTest() {
		LocalDate date = LocalDate.now();
		facade.getTotalMarketPrice(ASSET_ID, date);
		verify(service, times(1)).getTotalMarketPrice(ASSET_ID, date);
	}
	
	@Test
	public void getIncomeTest() {
		LocalDate date = LocalDate.now();
		facade.getIncome(ASSET_ID, date);
		verify(service, times(1)).getIncome(ASSET_ID, date);
	}
	
	@Test
	public void getProfitTest() {
		LocalDate date = LocalDate.now();
		facade.getProfit(ASSET_ID, date);
		verify(service, times(1)).getProfit(ASSET_ID, date);
	}
	
	@Test
	public void includeMarketPriceTest() {
		LocalDate date = LocalDate.now();
		BigDecimal price = new BigDecimal(1);
		facade.includeMarketPrice(ASSET_ID, price, date);
		verify(service, times(1)).includeMarketPrice(ASSET_ID, price, date);
	}
	
	@Test
	public void excludeMarketPriceTest() {
		String date = LocalDate.now().format(formatter);
		facade.excludeMarketPrice(ASSET_ID, date);
		verify(service, times(1)).excludeMarketPrice(ASSET_ID, date);
	}
	
	@Test
	public void getMovementsTest() {
		String dateBegin = LocalDate.now().format(formatter);
		String dateEnd = LocalDate.now().format(formatter);
		facade.getMovements(ASSET_ID, dateBegin, dateEnd);
		verify(service, times(1)).getMovements(ASSET_ID, dateBegin, dateEnd);
	}
	
	@Test
	public void includeMovementTest() {
		AssetMovement movement = AssetMovement.builder().build();
		facade.includeMovement(ACCOUNT_ID, ASSET_ID, movement);
		//verify(accountFacade, times(1)).includeLaunch(ACCOUNT_ID, movement);
		verify(service, times(1)).includeMovement(ASSET_ID, movement);
	}
	
	private Asset createAsset() {
		return Asset.builder()
				.id(ASSET_ID)
				.type(AssetType.RF)
				.issueDate(LocalDate.now().minusDays(1L))
				.dueDate(LocalDate.now().plusDays(1L))
				.build();
	}

}
