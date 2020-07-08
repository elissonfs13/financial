package com.maps.financial.domain.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maps.financial.domain.asset.AssetMovement;
import com.maps.financial.domain.asset.MovementType;

@Component
public class AccountFacade {
	
	@Autowired
	private AccountService assetService;
	
	@Autowired
	private LaunchFactory launchFactory;
	
	public Account findById(final Long id){
		return assetService.findById(id);
	}
	
	public List<Account> findAll() {
		return assetService.findAll();
	}
	
	public Account includeLaunch(final Long id, final Launch newLaunch) {
		return assetService.includeLaunch(id, newLaunch);
	}
	
	public Account includeLaunch(final Long id, final AssetMovement assetMovement) {
		Launch newLaunch = launchFactory.generateLaunch("description", getLaunchTypeByMovementType(assetMovement.getType()), assetMovement.getValue());
		return assetService.includeLaunch(id, newLaunch);
	}
	
	private LaunchType getLaunchTypeByMovementType(MovementType movementeType) {
		if (MovementType.BUY.equals(movementeType)) {
			return LaunchType.OUTBOUND;
		} else if (MovementType.BUY.equals(movementeType)) {
			return LaunchType.INBOUND;
		} else {
			return null;
		}
	}

}
