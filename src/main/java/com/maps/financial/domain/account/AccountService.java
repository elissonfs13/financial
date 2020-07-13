package com.maps.financial.domain.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maps.financial.domain.user.User;
import com.maps.financial.exceptions.AuthorizationException;
import com.maps.financial.exceptions.ExceptionMessage;
import com.maps.financial.exceptions.ObjectNotFoundException;
import com.maps.financial.infra.security.SecurityUtils;

@Service
public class AccountService {
	
	@Autowired
	private AccountRepository repository;
	
	@Autowired
	private SecurityUtils securityUtils;
	
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
	
	public Account includeLaunch(final Launch newLaunch) {
		//Validação: usuário administrativo não deve poder gerar lançamentos e movimentos
		if (isUserAdministrator()) {
			throw new AuthorizationException(ExceptionMessage.MESSAGE_ACCESS_DENIED);
		}
		
		Account account = findById(getAccountIdOfCurrentUser());
		if (newLaunch != null) {
			account.includeLaunch(newLaunch);
		}
		return account;
	}
	
	public BigDecimal getBalance(String data) {
		if (data == null) {
			return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN);
		}
		LocalDate date = LocalDate.parse(data, formatter);
		Account account = findById(getAccountIdOfCurrentUser());
		return account.getBalanceInDate(date);
	}

	public List<Launch> getLaunches(String dataInicio, String dataFim) {
		LocalDate dateBegin = LocalDate.parse(dataInicio, formatter);
		LocalDate dateEnd = LocalDate.parse(dataFim, formatter);
		Account account = findById(getAccountIdOfCurrentUser());
		List<Launch> launches = account.getLaunches()
				.stream()
				.filter(launch -> !launch.getDate().isBefore(dateBegin) && !launch.getDate().isAfter(dateEnd)) 
				.collect(Collectors.toList());
		
		return launches;
	}
	
	/**
	 * Método responsável por verificar se o usuário logado possui função de administrador
	 * 
	 * @param proceedByAdmin
	 * @return boolean
	 */
	private boolean isUserAdministrator() {
		return securityUtils.currentUserIsAdmin();
	}
	
	/**
	 * Método responsável por pegar o Id da conta corrente vinculada ao usuário logado
	 * 
	 * @return Long
	 */
	private Long getAccountIdOfCurrentUser() {
		final User user = securityUtils.getCurrentUser();
		return user.getAccountId();
	}

}
