package com.mckinsey.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mckinsey.dtos.UserDTO;
import com.mckinsey.entites.User;
import com.mckinsey.repositories.UserRepository;
import com.mckinsey.utilities.JwtUtil;

import jakarta.annotation.PostConstruct;

@Service
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	
	@Value("${app.user}")
	private String appUser;
	
	
	// Load initial user
	@PostConstruct
	public void createUsers() {
		User employee = new User();
		employee.setUsername(appUser);
		employee.setPassword(passwordEncoder.encode(appUser));
		userRepository.save(employee);
		
	}
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}
	
	public UserDTO getUserByToken(String token) {
		String userName = "";
		try {
			userName = jwtUtil.getUserNameFromToken(token);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid token!");
		}
		UserDTO dto = new UserDTO();
		if(!userName.isEmpty()) {
			User user = userRepository.findByUsername(userName);
			
			dto.setUsername(user.getUsername());
			dto.setPassword("******");
			
		}
		return dto;
		
	}
	public UserDTO createUser(UserDTO user) {
		try {
			UserDetails userDetails = loadUserByUsername(user.getUsername());
			if(userDetails != null)
				throw new IllegalArgumentException("Username already exist!");
		} catch (UsernameNotFoundException e) {
			// Nothing to do
		}
		
		User newUser = new User();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(passwordEncoder.encode(user.getPassword()));
		
		userRepository.save(newUser);
		
		return user;
	}
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				Collections.emptyList());
	}

}
