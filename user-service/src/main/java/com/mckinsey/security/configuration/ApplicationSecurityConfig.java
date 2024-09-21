package com.mckinsey.security.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ApplicationSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(requests -> requests.requestMatchers("/user/**").permitAll()
						.requestMatchers("/h2-console/**").permitAll().anyRequest().authenticated())
				.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

		return httpSecurity.build();

	}

}
