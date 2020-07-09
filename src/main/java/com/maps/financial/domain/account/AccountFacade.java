package com.maps.financial.domain.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.maps.financial.domain.asset.AssetMovement;
import com.maps.financial.domain.asset.MovementType;

@Component
public class AccountFacade {
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private LaunchFactory launchFactory;
	
	private static final String DESCRIPTION_BUY = "LAUNCH FOR A BUY";
	private static final String DESCRIPTION_SELL = "LAUNCH FOR A SELL";
	
	public Account findById(final Long id){
		return accountService.findById(id);
	}
	
	public List<Account> findAll() {
		return accountService.findAll();
	}
	
	@Transactional
	public Account includeLaunch(final Long id, final Launch newLaunch) {
		return accountService.includeLaunch(id, newLaunch);
	}
	
	@Transactional
	public Account includeLaunch(final Long id, final AssetMovement assetMovement) {
		Launch newLaunch = this.getLaunchByMovementType(assetMovement);
		return accountService.includeLaunch(id, newLaunch);
	}
	
	private Launch getLaunchByMovementType(final AssetMovement assetMovement) {
		if (MovementType.BUY.equals(assetMovement.getType())) {
			return launchFactory.generateLaunch(DESCRIPTION_BUY, LaunchType.OUTBOUND, assetMovement.getValue());
		} else if (MovementType.SELL.equals(assetMovement.getType())) {
			return launchFactory.generateLaunch(DESCRIPTION_SELL, LaunchType.INBOUND, assetMovement.getValue());
		} else {
			return null;
		}
	}

}
