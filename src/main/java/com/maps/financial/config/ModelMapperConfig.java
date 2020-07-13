package com.maps.financial.config;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * Configuração do ModelMapper
 * 
 * @author Elisson
 * @date 13/07/2020
 *
 */
@Configuration
public class ModelMapperConfig {
	
	/**
	 * Configuration for ModelMapper
	 * 
	 * @return ModelMapper
	 */
	@Bean
	public ModelMapper modelMapper() {
		final ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		return modelMapper;
	}
	
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer filters() {
		return new Jackson2ObjectMapperBuilderCustomizer() {

			@Override
			public void customize(final Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
				final SimpleFilterProvider filterProvider = new SimpleFilterProvider();
				jacksonObjectMapperBuilder.filters(filterProvider);
			}
		};
	}
}
