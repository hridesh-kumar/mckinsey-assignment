package com.mckinsey.security.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig {

	private final JwtAuthenticationFilter filter;

	public ApplicationSecurityConfig(JwtAuthenticationFilter filter) {
		this.filter = filter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(
				requests -> requests.requestMatchers(new AntPathRequestMatcher("/api/calculate/**")).authenticated())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
		return http.build();

	}

}
