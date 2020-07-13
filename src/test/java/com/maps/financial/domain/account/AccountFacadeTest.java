package com.maps.financial.domain.account;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.maps.financial.domain.asset.AssetMovement;
import com.maps.financial.domain.asset.MovementType;

@RunWith(MockitoJUnitRunner.class)
public class AccountFacadeTest {
	
	@InjectMocks
	private AccountFacade facade;
	
	@Mock
	private AccountService service;
	
	@Mock
	private LaunchFactory launchFactory;
	
	private static final Long ACCOUNT_ID = 1L;
	private static final String DESCRIPTION_BUY = "LAUNCH FOR A BUY";
	private static final String DESCRIPTION_SELL = "LAUNCH FOR A SELL";
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@Before
	public void init() {

	}
	
	@Test
	public void findByIdTest() {
		facade.findById(ACCOUNT_ID);
		verify(service, times(1)).findById(ACCOUNT_ID);
	}
	
	@Test
	public void findAllTest() {
		facade.findAll();
		verify(service, times(1)).findAll();
	}
	
	@Test
	public void getBalanceTest() {
		String date = LocalDate.now().format(formatter);
		facade.getBalance(date);
		verify(service, times(1)).getBalance(date);
	}
	
	@Test
	public void includeLaunchWithLaunchTest() {
		Launch launch = Launch.builder().build();
		facade.includeLaunch(launch);
		verify(service, times(1)).includeLaunch(launch);
	}
	
	@Test
	public void getLaunchesTest() {
		String dateBegin = LocalDate.now().format(formatter);
		String dateEnd = LocalDate.now().format(formatter);
		facade.getLaunches(dateBegin, dateEnd);
		verify(service, times(1)).getLaunches(dateBegin, dateEnd);
	}
	
	@Test
	public void includeLaunchWithAssetMovementBuyTest() {
		BigDecimal value = new BigDecimal(1.00).setScale(2, BigDecimal.ROUND_DOWN);
		Launch launch = Launch.builder().build();
		LocalDate date = LocalDate.now();
		AssetMovement movement = AssetMovement.builder().type(MovementType.BUY).value(value).date(date).build();
		when(launchFactory.generateLaunch(Mockito.anyString(), Mockito.any(LaunchType.class), 
				Mockito.any(BigDecimal.class), Mockito.any(LocalDate.class))).thenReturn(launch);
		
		facade.includeLaunch(movement);
		verify(launchFactory, times(1)).generateLaunch(Mockito.anyString(), Mockito.any(LaunchType.class), 
				Mockito.any(BigDecimal.class), Mockito.any(LocalDate.class));
		verify(launchFactory, times(1)).generateLaunch(DESCRIPTION_BUY, LaunchType.OUTBOUND, value, date);
		verify(service, times(1)).includeLaunch(Mockito.any(Launch.class));
		verify(service, times(1)).includeLaunch(launch);
	}
	
	@Test
	public void includeLaunchWithAssetMovementSellTest() {
		BigDecimal value = new BigDecimal(1.00).setScale(2, BigDecimal.ROUND_DOWN);
		Launch launch = Launch.builder().build();
		LocalDate date = LocalDate.now();
		AssetMovement movement = AssetMovement.builder().type(MovementType.SELL).value(value).date(date).build();
		when(launchFactory.generateLaunch(Mockito.anyString(), Mockito.any(LaunchType.class), 
				Mockito.any(BigDecimal.class), Mockito.any(LocalDate.class))).thenReturn(launch);
		
		facade.includeLaunch(movement);
		verify(launchFactory, times(1)).generateLaunch(Mockito.anyString(), Mockito.any(LaunchType.class), 
				Mockito.any(BigDecimal.class), Mockito.any(LocalDate.class));
		verify(launchFactory, times(1)).generateLaunch(DESCRIPTION_SELL, LaunchType.INBOUND, value, date);
		verify(service, times(1)).includeLaunch(Mockito.any(Launch.class));
		verify(service, times(1)).includeLaunch(launch);
	}
	
	@Test
	public void includeLaunchWithAssetMovementConsultTest() {
		AssetMovement movement = AssetMovement.builder().type(MovementType.CONSULT).build();
		facade.includeLaunch(movement);
		verify(launchFactory, times(0)).generateLaunch(Mockito.anyString(), Mockito.any(LaunchType.class), 
				Mockito.any(BigDecimal.class), Mockito.any(LocalDate.class));
		verify(service, times(1)).includeLaunch(Mockito.isNull());
		verify(service, times(1)).includeLaunch(null);
	}

}
