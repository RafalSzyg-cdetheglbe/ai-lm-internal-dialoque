package com.llmagent.lmagent.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import nl.dannyj.mistral.MistralClient;

@Configuration
public class AppConfig
{
	@Bean
	public MistralClient mistralClient() {
		return new MistralClient(System.getenv("API_KEY"));
	}
}
