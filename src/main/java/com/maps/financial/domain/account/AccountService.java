package com.maps.financial.domain.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maps.financial.exceptions.ObjectNotFoundException;

@Service
public class AccountService {
	
	@Autowired
	private AccountRepository repository;
	
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	public Account findById(final Long id)  throws ObjectNotFoundException {
		return repository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, Account.class));
	}
	
	public List<Account> findAll() {
		return repository.findAll();
	}

	public Account create(Account account) {
		return repository.save(account);
	}
	
	public Account includeLaunch(final Long id, final Launch newLaunch) {
		Account account = findById(id);
		if (newLaunch != null) {
			account.includeLaunch(newLaunch);
		}
		return account;
	}
	
	public BigDecimal getBalance(final Long accountId, String data) {
		if (data == null) {
			return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
		}
		LocalDate date = LocalDate.parse(data, formatter);
		Account account = findById(accountId);
		return account.getBalanceInDate(date);
	}

	public List<Launch> getLaunches(Long accountId, String dataInicio, String dataFim) {
		LocalDate dateBegin = LocalDate.parse(dataInicio, formatter);
		LocalDate dateEnd = LocalDate.parse(dataFim, formatter);
		Account account = findById(accountId);
		List<Launch> launches = account.getLaunches()
				.stream()
				.filter(launch -> !launch.getDate().isBefore(dateBegin) && !launch.getDate().isAfter(dateEnd)) 
				.collect(Collectors.toList());
		
		return launches;
	}

}
