package com.mckinsey.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mckinsey.dtos.AuthRequest;
import com.mckinsey.dtos.AuthResponse;
import com.mckinsey.dtos.UserDTO;
import com.mckinsey.exception.AppException;
import com.mckinsey.service.UserService;
import com.mckinsey.utilities.JwtUtil;

@RestController
@RequestMapping("/user")
public class UserController {

	private AuthenticationManager authenticationManager;

	private UserService userDetailsService;

	private JwtUtil jwtUtil;

	public UserController(AuthenticationManager authenticationManager, UserService userDetailsService,
			JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtUtil = jwtUtil;
	}

	@GetMapping("/profile")
	public ResponseEntity<UserDTO> getUserByToken(@RequestParam String jwt) {
		UserDTO user = userDetailsService.getUserByToken(jwt);
		if (user != null)
			return new ResponseEntity<>(user, HttpStatus.OK);

		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@PostMapping
	public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO request) {

		userDetailsService.createUser(request);
		return new ResponseEntity<>(request, HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> createAuthenticationToken(@RequestBody AuthRequest authRequest)
			throws Exception {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
			if(authentication == null)
				throw new AppException("Incorrect username or password");
		} catch (BadCredentialsException e) {
			throw new AppException("Incorrect username or password");
		}

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
		String jwt = jwtUtil.generateToken(userDetails);
		AuthResponse jwtResponse = new AuthResponse();
		jwtResponse.setJwt(jwt);
		return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
	}

}
