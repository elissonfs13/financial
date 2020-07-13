package com.maps.financial.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface para operações com Conta Corrente
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
public interface AccountRepository extends JpaRepository<Account, Long>{

}
