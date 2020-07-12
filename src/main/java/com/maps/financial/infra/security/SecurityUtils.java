package com.maps.financial.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.maps.financial.domain.user.User;
import com.maps.financial.domain.user.UserService;

@Component
public class SecurityUtils {
	
	@Autowired
	private UserService userService;
	
	public User getCurrentUser() {
		String userString = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
		return userService.findByUsername(userString);
	}
	
	public boolean currentUserIsAdmin() {
		User user = getCurrentUser();
		return user.isAdmin();
	}

}
