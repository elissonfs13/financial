package com.maps.financial.domain.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maps.financial.exceptions.ObjectNotFoundException;

@Service
public class AccountService {
	
	@Autowired
	private AccountRepository repository;
	
	public Account findById(final Long id)  throws ObjectNotFoundException {
		return repository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, Account.class));
	}
	
	public List<Account> findAll() {
		return repository.findAll();
	}
	
	public Account includeLaunch(final Long id, final Launch newLaunch) {
		Account account = findById(id);
		account.includeLaunch(newLaunch);
		return account;
	}

}
