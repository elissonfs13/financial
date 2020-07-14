package com.maps.financial.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.logging.log4j.util.Strings;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.Base64Utils;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Esta classe contém métodos utilitários para os testes de integração. 
 * Todos os testes de integração deverão extender esta classe.
 * 
 * @author Elisson
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

	protected static final ResultMatcher CREATED = status().isCreated(); 
	protected static final ResultMatcher OK = status().isOk(); 
	protected static final ResultMatcher FORBIDDEN = status().isForbidden(); 
	protected static final ResultMatcher NOT_FOUND = status().isNotFound();
	protected static final ResultMatcher NOT_ACCEPTABLE = status().isNotAcceptable();
	protected static final ResultMatcher UNAUTHORIZED = status().isUnauthorized();
	
	protected static final String TOKEN_ADMIN = "root:spiderman";
	protected static final String TOKEN_USER = "usuario-teste:integracao";
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	/**
	 * Realiza a conversão de um JSON em um objeto utilizando o
	 * <code>ObjectMapper</code>
	 * 
	 * @param mvcResult
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	protected <T> T getResultObject(final MvcResult mvcResult, final Class<T> clazz) throws Exception {
		final String contentAsString = mvcResult.getResponse().getContentAsString();
		if (clazz != null && Strings.isNotBlank(contentAsString)) {
			return mapper.readValue(contentAsString, clazz);
		}
		return null;
	}

	/**
	 * Realiza uma requisição do tipo GET
	 * 
	 * @param url URL do endpoint
	 * @param token Usuário e senha para geração do token de autenticação
	 * @param resultMatcher Resultado esperado (OK, FORBIDDEN, BAD_REQUEST, CREATED)
	 * @param clazz Tipo do retorno
	 * @param params Parametros que serão adicionados a URL ?param=...
	 * @param uriVars Variáveis da URL, ex: /assets/{assetId}
	 * @return
	 * @throws Exception
	 */
	protected <T> T getForObject(final String url, final String token, final ResultMatcher resultMatcher, final Class<T> clazz,
			final MultiValueMap<String, String> params, final Object... uriVars) throws Exception {
		return getResultObject(mockMvc.perform(get(url, uriVars)
				.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString(token.getBytes()))
				.params(params)).andExpect(resultMatcher).andReturn(), clazz);
	}
	
	/**
	 * Realiza uma requisição do tipo GET
	 * 
	 * @param url URL do endpoint
	 * @param token Usuário e senha para geração do token de autenticação
	 * @param resultMatcher Resultado esperado (OK, FORBIDDEN, BAD_REQUEST, CREATED)
	 * @param clazz Tipo do retorno
	 * @param uriVars Variáveis da URL, ex: /assets/{assetId}
	 * @return
	 * @throws Exception
	 */
	protected <T> T getForObject(final String url, final String token, final ResultMatcher resultMatcher, 
			final Class<T> clazz, final Object... uriVars) throws Exception {
		return getResultObject(mockMvc.perform(get(url, uriVars)
				.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString(token.getBytes())))
				.andExpect(resultMatcher).andReturn(), clazz);
	}
	
	/**
	 * Realiza uma requisição do tipo GET para endpoints que retornam um <code>Page</code>
	 * 
	 * @param url URL do endpoint
	 * @param token Usuário e senha para geração do token de autenticação
	 * @param resultMatcher Resultado esperado (OK, FORBIDDEN, BAD_REQUEST, CREATED)
	 * @param clazz Tipo do retorno
	 * @param params params Parametros que serão adicionados a URL ?param=...
	 * @param uriVars Variáveis da URL, ex: /assets/{assetId}
	 * @return
	 * @throws Exception
	 */
	protected <T> T getForList(final String url, final String token, final ResultMatcher resultMatcher, final Class<T> clazz,
			final MultiValueMap<String, String> params, final Object... uriVars) throws Exception {
		final MvcResult result = mockMvc.perform(get(url, uriVars)
				.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString(token.getBytes()))
				.params(params)).andExpect(resultMatcher).andReturn();
		return mapper.readValue(result.getResponse().getContentAsString(), clazz);
	}

	/**
	 * Realiza uma requisição do tipo POST
	 * 
	 * @param url URL do endpoint
	 * @param token Usuário e senha para geração do token de autenticação
	 * @param content Conteudo da requisição
	 * @param resultMatcher Resultado esperado (OK, FORBIDDEN, BAD_REQUEST, CREATED)
	 * @param clazz Tipo do retorno
	 * @param uriVars Variáveis da URL, ex: /assets/{assetId}
	 * @return
	 * @throws Exception
	 */
	protected <T> T postForObject(final String url, final String token, final Object content, final ResultMatcher resultMatcher,
			final Class<T> clazz, final Object... uriVars) throws Exception {
		return getResultObject(mockMvc.perform(
				post(url, uriVars).header(HttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString(token.getBytes()))
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(content)))
				.andExpect(resultMatcher).andReturn(), clazz);
	}

	/**
	 * Realiza uma requisição do tipo PUT
	 * 
	 * @param url URL do endpoint
	 * @param token Usuário e senha para geração do token de autenticação
	 * @param content Conteudo da requisição
	 * @param resultMatcher Resultado esperado (OK, FORBIDDEN, BAD_REQUEST, CREATED)
	 * @param clazz Tipo do retorno
	 * @param uriVars Variáveis da URL, ex: /assets/{assetId}
	 * @return
	 * @throws Exception
	 */
	protected <T> T putForObject(final String url, final String token, final Object content, 
			final ResultMatcher resultMatcher, final Class<T> clazz, final Object... uriVars) throws Exception {
		return getResultObject(mockMvc.perform(
				put(url, uriVars).contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString(token.getBytes()))
				.content(mapper.writeValueAsString(content)))
				.andExpect(resultMatcher).andReturn(), clazz);
	}
	
	/**
	 * Realiza uma requisição do tipo PUT
	 * 
	 * @param url URL do endpoint
	 * @param token Usuário e senha para geração do token de autenticação
	 * @param resultMatcher Resultado esperado (OK, FORBIDDEN, BAD_REQUEST, CREATED)
	 * @param clazz Tipo do retorno
	 * @param params params Parametros que serão adicionados a URL ?param=...
	 * @param uriVars Variáveis da URL
	 * @return
	 * @throws Exception
	 */
	protected <T> T putForObject(final String url, final String token, final ResultMatcher resultMatcher, final Class<T> clazz, 
			final MultiValueMap<String, String> params, final Object... uriVars) throws Exception {
		return getResultObject(mockMvc.perform(put(url, uriVars)
				.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString(token.getBytes()))
				.params(params)).andExpect(resultMatcher).andReturn(), clazz);
	}
	
	
	
	/**
	 * Realiza uma requisição do tipo DELETE
	 * 
	 * @param url URL do endpoint
	 * @param token Usuário e senha para geração do token de autenticação
	 * @param resultMatcher Resultado esperado (OK, FORBIDDEN, BAD_REQUEST, CREATED)
	 * @param clazz Tipo do retorno
	 * @param uriVars Variáveis da URL
	 * @return
	 * @throws Exception
	 */
	protected <T> T deleteObject(final String url, final String token, final ResultMatcher resultMatcher, 
			final Class<T> clazz, final Object... uriVars) throws Exception {
		return getResultObject(mockMvc.perform(delete(url, uriVars)
				.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString(token.getBytes())))
				.andExpect(resultMatcher).andReturn(), clazz);
	}

	
}
