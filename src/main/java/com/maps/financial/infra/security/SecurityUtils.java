package com.maps.financial.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.maps.financial.domain.user.User;
import com.maps.financial.domain.user.UserService;

/**
 * Utilitário que retorna informações do usuário autenticado na aplicação
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
@Component
public class SecurityUtils {
	
	@Autowired
	private UserService userService;
	
	/**
	 * Retorna o Usuário autenticado na aplicação
	 * 
	 * @return User
	 */
	public User getCurrentUser() {
		String userString = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
		return userService.findByUsername(userString);
	}
	
	/**
	 * Realiza a verificação se o usuário autenticado na aplicação possui função de administrador
	 * 
	 * @return boolean
	 */
	public boolean currentUserIsAdmin() {
		User user = getCurrentUser();
		return user.isAdmin();
	}

}
