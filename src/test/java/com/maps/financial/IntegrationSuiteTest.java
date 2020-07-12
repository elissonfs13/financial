package com.maps.financial;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.maps.financial.integration.AccountIntegrationTest;
import com.maps.financial.integration.AssetIntegrationTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	AccountIntegrationTest.class,
	AssetIntegrationTest.class
})
public class IntegrationSuiteTest {

}
