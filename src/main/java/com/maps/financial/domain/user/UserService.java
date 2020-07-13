package com.maps.financial.domain.user;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maps.financial.domain.account.Account;
import com.maps.financial.domain.account.AccountFacade;

/**
 * Classe de serviços para Usuários
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
@Service
public class UserService {
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private AccountFacade accountFacade;
	
	/**
	 * Devem ser pré-cadastrados usuários de "usuario0" até "usuario9", com senhas "senha0" até "senha9".
	 * O usuário administrativo deve ser chamado "root" com senha "spiderman".
	 */
	@PostConstruct
    public void init() {
		preRegistration("root", "spiderman", JobFunction.ADMIN, new BigDecimal(100.00));
		for (Integer i = 0; i < 10; i++) {
			preRegistration("usuario".concat(i.toString()), "senha".concat(i.toString()), 
					JobFunction.USER, BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_DOWN));
		}
		// Usuário criado para o teste de integração
		preRegistration("usuario-teste", "integracao", JobFunction.USER, new BigDecimal(1000.00));
    }
	
	/**
	 * Criação de um novo usuário a partir das informações passadas
	 * 
	 * @param username
	 * @param password
	 * @param jobFunction
	 * @return User
	 */
	public User create(final String username, final String password, final JobFunction jobFunction) {
		final User user = createUser(username, password, jobFunction);
		return repository.save(user);
	}
	
	/**
	 * Busca por um usuário que possui o username informado
	 * Utilizado no processo de autenticação
	 * 
	 * @param username
	 * @return User
	 */
	public User findByUsername(final String username) {
		return repository.findByUsername(username);
	}
	
	/**
	 * Desenvolvido para realização do pré cadastro de usuários
	 * 
	 * @param username
	 * @param password
	 * @param jobFunction
	 * @param initialBalance
	 */
	private void preRegistration(final String username, final String password, final JobFunction jobFunction, 
			final BigDecimal initialBalance) {
		final User user = create(username, password, jobFunction);
		Account account = createAccount(initialBalance, user);
		user.setAccount(account);
	}
	
	/**
	 * Desenvolvido para realização do pré cadastro de usuários
	 * 
	 * @param username
	 * @param password
	 * @param jobFunction
	 * @return User
	 */
	private User createUser(final String username, final String password, final JobFunction jobFunction) {
		return User.builder()
				.username(username)
				.password(password)
				.jobFunction(jobFunction)
				.build();
	}
	
	/**
	 * Desenvolvido para realização do pré cadastro de usuários
	 * 
	 * @param initialBalance
	 * @param user
	 * @return Account
	 */
	private Account createAccount(final BigDecimal initialBalance, final User user) {
		Account account = accountFacade.create(Account.builder()
				.balance(initialBalance)
				.user(user)
				.build());
		return account;
	}

}
