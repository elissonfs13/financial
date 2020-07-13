package com.maps.financial.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.maps.financial.infra.security.SimpleUserAuthenticationProvider;

/**
 * Configuração de segurança para autenticação simples.
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private SimpleUserAuthenticationProvider provider;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
	        .authorizeRequests()
	        .antMatchers("/h2-console/**").permitAll()
	        .anyRequest().authenticated()
	        .and().httpBasic();
	}
	
	@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.authenticationProvider(provider);
	
	    auth.inMemoryAuthentication()
	            .withUser("admin")
	            .password("{noop}admin")
	            .authorities("ADMIN");
	
	}
	
}