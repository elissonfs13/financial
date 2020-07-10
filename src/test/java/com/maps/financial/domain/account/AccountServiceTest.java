package com.maps.financial.domain.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.maps.financial.exceptions.AccountBalanceNotAvailable;
import com.maps.financial.exceptions.ObjectNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {
	
	@Mock
	private AccountRepository repository;
	
	@InjectMocks
	private AccountService service;
	
	private static final Long ACCOUNT_ID = 1L;
	private static final String DESCRIPTION = "DESCRIPTION";
	private Account account;
	private List<Account> accounts;
	
	@Before
	public void init() {
		account = createAccount();
		accounts = new ArrayList<>();
		accounts.add(account);
	}

	@Test
	public void findByIdTest(){
		final Optional<Account> optional = Optional.of(account);	
		when(repository.findById(ACCOUNT_ID)).thenReturn(optional);	
		final Account accountReturned = service.findById(ACCOUNT_ID);
		assertNotNull(accountReturned);
		assertEquals(account, accountReturned);
		verify(repository, times(1)).findById(ACCOUNT_ID);
	}
	
	@Test(expected = ObjectNotFoundException.class)
	public void findByIdWithObjectNotFoundExceptionTest(){
		service.findById(2L);
	}
	
	@Test
	public void findAllTest() {
		when(repository.findAll()).thenReturn(accounts);
		final List<Account> listAccounts = service.findAll();			
		assertEquals(1, listAccounts.size());	
		verify(repository, times(1)).findAll();
	}
	
	@Test
	public void includeLaunchInboundTest() {
		Launch launch = createLaunch(LaunchType.INBOUND, 10.00, LocalDate.of(2020, 7, 9));
		final Optional<Account> optional = Optional.of(account);	
		when(repository.findById(ACCOUNT_ID)).thenReturn(optional);	
		final Account accountReturned = service.includeLaunch(ACCOUNT_ID, launch);
		assertNotNull(accountReturned);
		assertEquals(1, accountReturned.getLaunches().size());
		assertEquals(launch, accountReturned.getLaunches().get(0));
	}
	
	@Test
	public void includeLaunchOutboundTest() {
		Launch launch = createLaunch(LaunchType.OUTBOUND, 10.00, LocalDate.of(2020, 7, 9));
		final Optional<Account> optional = Optional.of(account);	
		when(repository.findById(ACCOUNT_ID)).thenReturn(optional);	
		final Account accountReturned = service.includeLaunch(ACCOUNT_ID, launch);
		assertNotNull(accountReturned);
		assertEquals(1, accountReturned.getLaunches().size());
		assertEquals(launch, accountReturned.getLaunches().get(0));
	}
	
	@Test(expected = AccountBalanceNotAvailable.class)
	public void includeLaunchOutboundWithUnavailableBalanceTest() {
		Launch launch = createLaunch(LaunchType.OUTBOUND, 30.00, LocalDate.of(2020, 7, 9));
		final Optional<Account> optional = Optional.of(account);	
		when(repository.findById(ACCOUNT_ID)).thenReturn(optional);	
		service.includeLaunch(ACCOUNT_ID, launch);
	}
	
	@Test
	public void getBalanceTest() {
		account.includeLaunch(createLaunch(LaunchType.INBOUND, 20.00, LocalDate.of(2020, 7, 9)));
		account.includeLaunch(createLaunch(LaunchType.INBOUND, 18.70, LocalDate.of(2020, 7, 9)));
		account.includeLaunch(createLaunch(LaunchType.OUTBOUND, 5.20, LocalDate.of(2020, 7, 9)));
		account.includeLaunch(createLaunch(LaunchType.INBOUND, 0.80, LocalDate.of(2020, 7, 9)));
		account.includeLaunch(createLaunch(LaunchType.OUTBOUND, 5.20, LocalDate.of(2020, 7, 11))); //Não deve ser considerado no cálculo
		account.includeLaunch(createLaunch(LaunchType.OUTBOUND, 5.20, LocalDate.of(2020, 7, 11))); //Não deve ser considerado no cálculo
		final Optional<Account> optional = Optional.of(account);	
		when(repository.findById(ACCOUNT_ID)).thenReturn(optional);
		BigDecimal valueReturned = service.getBalance(ACCOUNT_ID, LocalDate.of(2020, 7, 10));
		assertNotNull(valueReturned);
		assertEquals(formatBigDecimalScale(34.30), valueReturned);	
	}
	
	@Test
	public void getBalanceWithoutDateTest() {
		account.includeLaunch(createLaunch(LaunchType.INBOUND, 20.00, LocalDate.of(2020, 7, 9)));
		final Optional<Account> optional = Optional.of(account);	
		when(repository.findById(ACCOUNT_ID)).thenReturn(optional);
		BigDecimal valueReturned = service.getBalance(ACCOUNT_ID, null);
		assertNotNull(valueReturned);
		assertEquals(formatBigDecimalScale(0.00), valueReturned);	
	}
	
	@Test
	public void includeLaunchNullTest() {
		final Optional<Account> optional = Optional.of(account);	
		when(repository.findById(ACCOUNT_ID)).thenReturn(optional);	
		final Account accountReturned = service.includeLaunch(ACCOUNT_ID, null);
		assertNotNull(accountReturned);
		assertEquals(0, accountReturned.getLaunches().size());
	}
	
	@Test
	public void getLaunchesTest() {
		account.includeLaunch(createLaunch(LaunchType.INBOUND, 20.00, LocalDate.of(2020, 7, 6)));
		account.includeLaunch(createLaunch(LaunchType.INBOUND, 18.70, LocalDate.of(2020, 7, 7)));
		account.includeLaunch(createLaunch(LaunchType.OUTBOUND, 5.20, LocalDate.of(2020, 7, 8)));
		account.includeLaunch(createLaunch(LaunchType.INBOUND, 0.80, LocalDate.of(2020, 7, 9)));
		account.includeLaunch(createLaunch(LaunchType.OUTBOUND, 7.20, LocalDate.of(2020, 7, 10))); 
		account.includeLaunch(createLaunch(LaunchType.OUTBOUND, 5.20, LocalDate.of(2020, 7, 11)));
		final Optional<Account> optional = Optional.of(account);	
		when(repository.findById(ACCOUNT_ID)).thenReturn(optional);	
		final List<Launch> launches = service.getLaunches(ACCOUNT_ID, LocalDate.of(2020, 7, 8), LocalDate.of(2020, 7, 10));
		assertNotNull(launches);
		assertEquals(3, launches.size());	
		assertEquals(formatBigDecimalScale(5.20), launches.get(0).getValue());
		assertEquals(formatBigDecimalScale(7.20), launches.get(2).getValue());
	}
	
	private BigDecimal formatBigDecimalScale(Double returnedValue) {
		BigDecimal bigDecimalFormated = new BigDecimal(returnedValue);
		return bigDecimalFormated.setScale(2, BigDecimal.ROUND_DOWN);
	}
	
	private Account createAccount() {
		return Account.builder()
				.id(ACCOUNT_ID)
				.balance(new BigDecimal(20.50))
				.build();
	}
	
	private Launch createLaunch(LaunchType type, Double value, LocalDate date) {
		return Launch.builder()
				.description(DESCRIPTION)
				.type(type)
				.value(new BigDecimal(value))
				.date(date)
				.build();
	}

}
