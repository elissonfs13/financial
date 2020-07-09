package com.maps.financial.domain.asset;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
	public void includeMovementTest() {
		AssetMovement movement = AssetMovement.builder().build();
		facade.includeMovement(ACCOUNT_ID, ASSET_ID, movement);
		verify(accountFacade, times(1)).includeLaunch(ACCOUNT_ID, movement);
		verify(service, times(1)).includeMovement(ASSET_ID, movement);
	}
	
	private Asset createAsset() {
		return Asset.builder()
				.id(ASSET_ID)
				.type(AssetType.RF)
				.build();
	}

}
