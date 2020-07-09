package com.maps.financial.resources.account;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.maps.financial.domain.account.Account;
import com.maps.financial.domain.account.AccountFacade;
import com.maps.financial.domain.account.Launch;
import com.maps.financial.resources.account.dto.AccountDTO;
import com.maps.financial.resources.account.dto.LaunchDTO;

@RestController
@RequestMapping("/contacorrente")
public class AccountResource {
	
	@Autowired
	private AccountFacade accountFacade;
	
	@Autowired
	private ModelMapper modelMapper;
	
	/**
	 * Endpoint REST para consulta de todas as contas correntes
	 * 
	 * @return List<AccountDTO>
	 */
	@GetMapping
	public ResponseEntity<List<AccountDTO>> findAll() {
		final List<Account> accounts = accountFacade.findAll();
		return ResponseEntity.ok()
				.body(accounts.stream()
						.map(account -> modelMapper.map(account, AccountDTO.class))
						.collect(Collectors.toList()));
	}
	
	/**
	 * Endpoint REST para busca de uma contacorrente específica
	 * 
	 * @param accountId
	 * @return AccountDTO
	 */
	@GetMapping("/{accountId}")
	public ResponseEntity<AccountDTO> findById(@PathVariable final Long accountId) {
		final Account account = accountFacade.findById(accountId);
		final AccountDTO accountDTO = modelMapper.map(account, AccountDTO.class);
		return ResponseEntity.ok().body(accountDTO);
	}
	
	/**
	 * Endpoint REST para cadastro de um novo lançamento de crédito/débito na conta corrente
	 * 
	 * @param assetDTO
	 * @return AssetDTO
	 */
	@PostMapping("/{accountId}/lancamento")
	public ResponseEntity<AccountDTO> includeLaunch(@PathVariable final Long accountId, @RequestBody final LaunchDTO launchDTO) {
		final Account account = accountFacade.includeLaunch(accountId, modelMapper.map(launchDTO, Launch.class));
		final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(account.getId()).toUri();
		return ResponseEntity
				.created(uri)
				.body(modelMapper.map(account, AccountDTO.class));
	}

}
