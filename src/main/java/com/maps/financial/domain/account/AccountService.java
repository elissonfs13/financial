package com.maps.financial.domain.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
		if (newLaunch != null) {
			account.includeLaunch(newLaunch);
		}
		return account;
	}
	
	public BigDecimal getBalance(final Long accountId, LocalDate date) {
		Account account = findById(accountId);
		return account.getBalanceInDate(date);
	}

	public List<Launch> getLaunches(Long accountId, LocalDate dateBegin, LocalDate dateEnd) {
		Account account = findById(accountId);
		List<Launch> launches = account.getLaunches()
				.stream()
				.filter(launch -> !launch.getDate().isBefore(dateBegin) && !launch.getDate().isAfter(dateEnd)) 
				.collect(Collectors.toList());
		
		return launches;
	}

}
