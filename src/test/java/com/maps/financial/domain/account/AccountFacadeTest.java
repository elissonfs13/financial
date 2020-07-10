package com.maps.financial.domain.account;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

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
		facade.getBalance(ACCOUNT_ID, LocalDate.now());
		verify(service, times(1)).getBalance(ACCOUNT_ID, LocalDate.now());
	}
	
	@Test
	public void includeLaunchWithLaunchTest() {
		Launch launch = Launch.builder().build();
		facade.includeLaunch(ACCOUNT_ID, launch);
		verify(service, times(1)).includeLaunch(ACCOUNT_ID, launch);
	}
	
	@Test
	public void getLaunchesTest() {
		LocalDate dateBegin = LocalDate.now();
		LocalDate dateEnd = LocalDate.now();
		facade.getLaunches(ACCOUNT_ID, dateBegin, dateEnd);
		verify(service, times(1)).getLaunches(ACCOUNT_ID, dateBegin, dateEnd);
	}
	
	@Test
	public void includeLaunchWithAssetMovementBuyTest() {
		BigDecimal value = new BigDecimal(1.00).setScale(2, BigDecimal.ROUND_DOWN);
		Launch launch = Launch.builder().build();
		LocalDate date = LocalDate.now();
		AssetMovement movement = AssetMovement.builder().type(MovementType.BUY).value(value).date(date).build();
		when(launchFactory.generateLaunch(Mockito.anyString(), Mockito.any(LaunchType.class), 
				Mockito.any(BigDecimal.class), Mockito.any(LocalDate.class))).thenReturn(launch);
		
		facade.includeLaunch(ACCOUNT_ID, movement);
		verify(launchFactory, times(1)).generateLaunch(Mockito.anyString(), Mockito.any(LaunchType.class), 
				Mockito.any(BigDecimal.class), Mockito.any(LocalDate.class));
		verify(launchFactory, times(1)).generateLaunch(DESCRIPTION_BUY, LaunchType.OUTBOUND, value, date);
		verify(service, times(1)).includeLaunch(Mockito.any(Long.class), Mockito.any(Launch.class));
		verify(service, times(1)).includeLaunch(ACCOUNT_ID, launch);
	}
	
	@Test
	public void includeLaunchWithAssetMovementSellTest() {
		BigDecimal value = new BigDecimal(1.00).setScale(2, BigDecimal.ROUND_DOWN);
		Launch launch = Launch.builder().build();
		LocalDate date = LocalDate.now();
		AssetMovement movement = AssetMovement.builder().type(MovementType.SELL).value(value).date(date).build();
		when(launchFactory.generateLaunch(Mockito.anyString(), Mockito.any(LaunchType.class), 
				Mockito.any(BigDecimal.class), Mockito.any(LocalDate.class))).thenReturn(launch);
		
		facade.includeLaunch(ACCOUNT_ID, movement);
		verify(launchFactory, times(1)).generateLaunch(Mockito.anyString(), Mockito.any(LaunchType.class), 
				Mockito.any(BigDecimal.class), Mockito.any(LocalDate.class));
		verify(launchFactory, times(1)).generateLaunch(DESCRIPTION_SELL, LaunchType.INBOUND, value, date);
		verify(service, times(1)).includeLaunch(Mockito.any(Long.class), Mockito.any(Launch.class));
		verify(service, times(1)).includeLaunch(ACCOUNT_ID, launch);
	}
	
	@Test
	public void includeLaunchWithAssetMovementConsultTest() {
		AssetMovement movement = AssetMovement.builder().type(MovementType.CONSULT).build();
		facade.includeLaunch(ACCOUNT_ID, movement);
		verify(launchFactory, times(0)).generateLaunch(Mockito.anyString(), Mockito.any(LaunchType.class), 
				Mockito.any(BigDecimal.class), Mockito.any(LocalDate.class));
		verify(service, times(1)).includeLaunch(Mockito.any(Long.class), Mockito.isNull());
		verify(service, times(1)).includeLaunch(ACCOUNT_ID, null);
	}

}
