package com.mckinsey.security.configuration;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mckinsey.dtos.UserDTO;
import com.mckinsey.exception.AppException;
import com.mckinsey.services.AuthenticationService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final AuthenticationService authservice;

	public JwtAuthenticationFilter(AuthenticationService authservice) {
		this.authservice = authservice;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		final String authorizationHeader = request.getHeader("Authorization");

		String jwt = null;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwt = authorizationHeader.substring(7);

		} else {
			throw new AppException("Token is missing!");
		}

		ResponseEntity<UserDTO> authResponse = authservice.getUserByToken(jwt);
		
		if (authResponse.getStatusCode() == HttpStatus.OK) {
			UserDTO user = authResponse.getBody();
			UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(),
					user.getPassword(),  Collections.emptyList());
			
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());

			usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			Map<String, String> principal = new HashMap<>();

			principal.put("username", user.getUsername());
			principal.put("jwt", jwt);

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
					null, Collections.emptyList());

			SecurityContextHolder.getContext().setAuthentication(authentication);

			chain.doFilter(request, response);

		}
	}

}
