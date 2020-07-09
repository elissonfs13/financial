package com.maps.financial;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.maps.financial.domain.account.AccountFacadeTest;
import com.maps.financial.domain.account.AccountServiceTest;
import com.maps.financial.domain.asset.AssetFacadeTest;
import com.maps.financial.domain.asset.AssetServiceTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	AccountFacadeTest.class,
	AccountServiceTest.class,
	AssetFacadeTest.class,
	AssetServiceTest.class
})
public class UnitSuiteTeste {

}
