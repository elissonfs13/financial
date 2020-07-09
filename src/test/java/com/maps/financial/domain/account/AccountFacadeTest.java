package com.maps.financial.domain.account;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

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
	public void includeLaunchWithLaunchTest() {
		Launch launch = Launch.builder().build();
		facade.includeLaunch(ACCOUNT_ID, launch);
		verify(service, times(1)).includeLaunch(ACCOUNT_ID, launch);
	}
	
	@Test
	public void includeLaunchWithAssetMovementBuyTest() {
		BigDecimal value = new BigDecimal(1.00);
		Launch launch = Launch.builder().build();
		AssetMovement movement = AssetMovement.builder().type(MovementType.BUY).value(value).build();
		when(launchFactory.generateLaunch(Mockito.anyString(), Mockito.any(LaunchType.class), Mockito.any(BigDecimal.class)))
					.thenReturn(launch);
		
		facade.includeLaunch(ACCOUNT_ID, movement);
		verify(launchFactory, times(1)).generateLaunch(Mockito.anyString(), Mockito.any(LaunchType.class), Mockito.any(BigDecimal.class));
		verify(launchFactory, times(1)).generateLaunch(DESCRIPTION_BUY, LaunchType.OUTBOUND, value);
		verify(service, times(1)).includeLaunch(Mockito.any(Long.class), Mockito.any(Launch.class));
		verify(service, times(1)).includeLaunch(ACCOUNT_ID, launch);
	}
	
	@Test
	public void includeLaunchWithAssetMovementSellTest() {
		BigDecimal value = new BigDecimal(1.00);
		Launch launch = Launch.builder().build();
		AssetMovement movement = AssetMovement.builder().type(MovementType.SELL).value(value).build();
		when(launchFactory.generateLaunch(Mockito.anyString(), Mockito.any(LaunchType.class), Mockito.any(BigDecimal.class)))
					.thenReturn(launch);
		
		facade.includeLaunch(ACCOUNT_ID, movement);
		verify(launchFactory, times(1)).generateLaunch(Mockito.anyString(), Mockito.any(LaunchType.class), Mockito.any(BigDecimal.class));
		verify(launchFactory, times(1)).generateLaunch(DESCRIPTION_SELL, LaunchType.INBOUND, value);
		verify(service, times(1)).includeLaunch(Mockito.any(Long.class), Mockito.any(Launch.class));
		verify(service, times(1)).includeLaunch(ACCOUNT_ID, launch);
	}
	
	@Test
	public void includeLaunchWithAssetMovementConsultTest() {
		AssetMovement movement = AssetMovement.builder().type(MovementType.CONSULT).build();
		facade.includeLaunch(ACCOUNT_ID, movement);
		verify(launchFactory, times(0)).generateLaunch(Mockito.anyString(), Mockito.any(LaunchType.class), Mockito.any(BigDecimal.class));
		verify(service, times(1)).includeLaunch(Mockito.any(Long.class), Mockito.isNull());
		verify(service, times(1)).includeLaunch(ACCOUNT_ID, null);
	}

}
