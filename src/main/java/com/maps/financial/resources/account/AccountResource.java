package com.maps.financial.resources.account;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.maps.financial.domain.account.Account;
import com.maps.financial.domain.account.AccountFacade;
import com.maps.financial.domain.account.Launch;
import com.maps.financial.resources.account.dto.AccountDTO;
import com.maps.financial.resources.account.dto.BalanceDTO;
import com.maps.financial.resources.account.dto.LaunchDTO;

/**
 * Resource para Conta Corrente
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
@RestController
@RequestMapping("/contacorrente")
public class AccountResource {
	
	@Autowired
	private AccountFacade accountFacade;
	
	@Autowired
	private ModelMapper modelMapper;
	
	/**
	 * Endpoint REST para cadastro de um novo lançamento de crédito na conta corrente do usuário logado
	 * 
	 * @param launchDTO
	 * @return LaunchDTO
	 */
	@PostMapping("/credito")
	public ResponseEntity<AccountDTO> includeLaunchInbound(@RequestBody final LaunchDTO launchDTO) {
		final Account account = accountFacade.includeLaunch(modelMapper.map(launchDTO, Launch.class));
		final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(account.getId()).toUri();
		return ResponseEntity
				.created(uri)
				.body(modelMapper.map(account, AccountDTO.class));
	}
	
	/**
	 * Endpoint REST para cadastro de um novo lançamento de débito na conta corrente do usuário logado
	 * 
	 * @param launchDTO
	 * @return LaunchDTO
	 */
	@PostMapping("/debito")
	public ResponseEntity<AccountDTO> includeLaunchOutbound(@RequestBody final LaunchDTO launchDTO) {
		final Account account = accountFacade.includeLaunch(modelMapper.map(launchDTO, Launch.class));
		final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(account.getId()).toUri();
		return ResponseEntity
				.created(uri)
				.body(modelMapper.map(account, AccountDTO.class));
	}
	
	/**
	 * Endpoint REST para busca de lançamentos entre datas específicas da conta corrente do usuário logado
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return List<LaunchDTO>
	 */
	@GetMapping("/lancamento")
	public ResponseEntity<List<LaunchDTO>> findLaunchesByDate(
			@RequestParam("dataInicio") String dataInicio, @RequestParam("dataFim") String dataFim) {
		final List<Launch> launches = accountFacade.getLaunches(dataInicio, dataFim);
		return ResponseEntity.ok()
				.body(launches.stream()
						.map(launch -> modelMapper.map(launch, LaunchDTO.class))
						.collect(Collectors.toList()));
	}
	
	/**
	 * Endpoint REST para consulta do saldo da conta corrente do usuário logado em uma data específica
	 * 
	 * @param data
	 * @return BalanceDTO
	 */
	@GetMapping("/saldo")
	public ResponseEntity<BalanceDTO> getBalanceInDate(@RequestParam("data") String data) {
		BigDecimal balance = accountFacade.getBalance(data);
		return ResponseEntity.ok().body(BalanceDTO.builder().saldo(balance).build());
	}

}
