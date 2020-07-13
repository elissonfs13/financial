package com.maps.financial.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.maps.financial.domain.account.LaunchType;
import com.maps.financial.resources.account.dto.AccountDTO;
import com.maps.financial.resources.account.dto.BalanceDTO;
import com.maps.financial.resources.account.dto.LaunchDTO;
import com.maps.financial.resources.commons.dto.DefaultErrorDTO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountIntegrationTest extends AbstractIntegrationTest {
	
	private static final String BASE_URL = "/contacorrente";
	private static final String LAUNCH_URL = BASE_URL + "/lancamento";
	private static final String LAUNCH_IN_URL = BASE_URL + "/credito";
	private static final String LAUNCH_OUT_URL = BASE_URL + "/debito";
	private static final String BALANCE_URL = BASE_URL + "/saldo";
	
	@Test
	public void stage001_includeLaunchByAdminTest() throws Exception {
		final LaunchDTO launch = createLaunch(LaunchType.INBOUND, LocalDate.of(2020, 7, 9), new BigDecimal(15.00));
		DefaultErrorDTO error = postForObject(LAUNCH_IN_URL, TOKEN_ADMIN, launch, FORBIDDEN, DefaultErrorDTO.class);
		assertNotNull(error);
	}
	
	@Test
	public void stage002_includeLaunch1Test() throws Exception {
		final LaunchDTO launch = createLaunch(LaunchType.INBOUND, LocalDate.of(2020, 7, 9), new BigDecimal(15.00));
		final AccountDTO result = postForObject(LAUNCH_IN_URL, TOKEN_USER, launch, CREATED, AccountDTO.class);
		assertNotNull(result);
        assertNotNull(result.getLaunches());
        assertEquals(1, result.getLaunches().size());
        assertEquals(launch.getDate(), result.getLaunches().get(0).getDate());
	}
	
	@Test
	public void stage003_includeLaunch2Test() throws Exception {
		final LaunchDTO launch = createLaunch(LaunchType.OUTBOUND, LocalDate.of(2020, 7, 10), new BigDecimal(3.50));
		final AccountDTO result = postForObject(LAUNCH_OUT_URL, TOKEN_USER, launch, CREATED, AccountDTO.class);
		assertNotNull(result);
        assertNotNull(result.getLaunches());
        assertEquals(2, result.getLaunches().size());
        assertEquals(launch.getDate(), result.getLaunches().get(1).getDate());
	}
	
	@Test
	public void stage004_includeLaunch3Test() throws Exception {
		final LaunchDTO launch = createLaunch(LaunchType.OUTBOUND, LocalDate.of(2020, 7, 15), new BigDecimal(10.20));
		final AccountDTO result = postForObject(LAUNCH_OUT_URL, TOKEN_USER, launch, CREATED, AccountDTO.class);
		assertNotNull(result);
        assertNotNull(result.getLaunches());
        assertEquals(3, result.getLaunches().size());
        assertEquals(launch.getDate(), result.getLaunches().get(2).getDate());
	}
	
	@Test
	public void stage005_includeLaunch4Test() throws Exception {
		final LaunchDTO launch = createLaunch(LaunchType.INBOUND, LocalDate.of(2020, 7, 20), new BigDecimal(11.45));
		final AccountDTO result = postForObject(LAUNCH_IN_URL, TOKEN_USER, launch, CREATED, AccountDTO.class);
		assertNotNull(result);
        assertNotNull(result.getLaunches());
        assertEquals(4, result.getLaunches().size());
        assertEquals(launch.getDate(), result.getLaunches().get(3).getDate());
	}
	
	@Test
	public void stage006_includeLaunch5Test() throws Exception {
		final LaunchDTO launch = createLaunch(LaunchType.INBOUND, LocalDate.of(2020, 7, 21), new BigDecimal(20.20));
		final AccountDTO result = postForObject(LAUNCH_IN_URL, TOKEN_USER, launch, CREATED, AccountDTO.class);
		assertNotNull(result);
        assertNotNull(result.getLaunches());
        assertEquals(5, result.getLaunches().size());
        assertEquals(launch.getDate(), result.getLaunches().get(4).getDate());
	}
	
	@Test
	public void stage007_findLaunchesByDateTest() throws Exception {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("dataInicio", "2020-07-10");
		params.add("dataFim", "2020-07-20");
		final List<LaunchDTO> launches = Arrays.asList(getForList(LAUNCH_URL, TOKEN_USER, OK, LaunchDTO[].class, params));
		assertNotNull(launches);
		assertEquals(3, launches.size());
	}
	
	@Test
	public void stage008_getBalanceInDateTest() throws Exception {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("data", "2020-07-15");
		final BalanceDTO balance = getForObject(BALANCE_URL, TOKEN_USER, OK, BalanceDTO.class, params);
		assertNotNull(balance);
		assertEquals(new BigDecimal(1001.30).setScale(2, BigDecimal.ROUND_HALF_EVEN), balance.getSaldo());
	}

	private LaunchDTO createLaunch(LaunchType type, LocalDate date, BigDecimal value) {
		return LaunchDTO.builder()
				.description("description")
				.value(value)
				.date(date)
				.type(type)
				.build();
	}

}
