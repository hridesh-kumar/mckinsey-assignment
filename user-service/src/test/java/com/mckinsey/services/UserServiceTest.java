package com.mckinsey.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mckinsey.dtos.UserDTO;
import com.mckinsey.entites.User;
import com.mckinsey.repositories.UserRepository;
import com.mckinsey.service.UserService;
import com.mckinsey.utilities.JwtUtil;

class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testLoadUserByUsername_UserExists() {
		User user = new User();
		user.setUsername("testUser");
		user.setPassword("testPass");

		when(userRepository.findByUsername("testUser")).thenReturn(user);

		UserDetails userDetails = userService.loadUserByUsername("testUser");

		assertNotNull(userDetails);
		assertEquals("testUser", userDetails.getUsername());
		assertEquals("testPass", userDetails.getPassword());
	}

	@Test
	void testLoadUserByUsername_UserDoesNotExist() {
		when(userRepository.findByUsername("nonExistentUser")).thenReturn(null);

		assertThrows(UsernameNotFoundException.class, () -> {
			userService.loadUserByUsername("nonExistentUser");
		});
	}

	@Test
	void testGetUserByToken_ValidToken() {
		String token = "validToken";
		String username = "testUser";
		User user = new User();
		user.setUsername(username);

		when(jwtUtil.getUserNameFromToken(token)).thenReturn(username);
		when(userRepository.findByUsername(username)).thenReturn(user);

		UserDTO userDTO = userService.getUserByToken(token);

		assertNotNull(userDTO);
		assertEquals(username, userDTO.getUsername());
		assertEquals("******", userDTO.getPassword());
	}

	@Test
	void testGetUserByToken_InvalidToken() {
		String token = "invalidToken";

		when(jwtUtil.getUserNameFromToken(token)).thenThrow(new IllegalArgumentException("Invalid token!"));

		assertThrows(IllegalArgumentException.class, () -> {
			userService.getUserByToken(token);
		});
	}

	@Test
	void testCreateUser_UserDoesNotExist() {
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername("newUser");
		userDTO.setPassword("newPass");

		when(userRepository.findByUsername("newUser")).thenReturn(null);
		when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");

		UserDTO createdUser = userService.createUser(userDTO);

		assertNotNull(createdUser);
		assertEquals("newUser", createdUser.getUsername());
		assertEquals("newPass", createdUser.getPassword());

		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void testCreateUser_UserAlreadyExists() {
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername("existingUser");

		User existingUser = new User();
		existingUser.setUsername("existingUser");

		when(userRepository.findByUsername("existingUser")).thenReturn(existingUser);

		assertThrows(IllegalArgumentException.class, () -> {
			userService.createUser(userDTO);
		});

		verify(userRepository, never()).save(any(User.class));
	}
}
