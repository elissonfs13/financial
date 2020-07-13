package com.maps.financial.infra.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.maps.financial.domain.user.User;
import com.maps.financial.domain.user.UserService;
import com.maps.financial.exceptions.ExceptionMessage;

@Component
public class SimpleUserAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private UserService userService;
	
    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        String username = auth.getName();
        String password = auth.getCredentials().toString();
        
        User user = userService.findByUsername(username);
        if (user != null) {
        	if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
        		return new UsernamePasswordAuthenticationToken(username, password, Collections.emptyList());
        	}
        }
 
        throw new BadCredentialsException(ExceptionMessage.MESSAGE_AUTHENTICATION_FAILED.name());
    }
 
    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}
