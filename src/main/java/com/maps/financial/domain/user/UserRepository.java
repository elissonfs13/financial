package com.maps.financial.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface para operações com Usuários
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {
	
	User findByUsername(String username);

}
