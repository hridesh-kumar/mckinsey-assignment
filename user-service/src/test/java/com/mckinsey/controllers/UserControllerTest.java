package com.mckinsey.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckinsey.dtos.AuthRequest;
import com.mckinsey.dtos.UserDTO;
import com.mckinsey.service.UserService;
import com.mckinsey.utilities.JwtUtil;

@WebMvcTest(UserController.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	private UserService userDetailsService;

	@MockBean
	private JwtUtil jwtUtil;

	@InjectMocks
	private UserController userController;

	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	@Test
	void testGetUserByToken() throws Exception {
		UserDTO user = new UserDTO();
		user.setUsername("hridesh");
		user.setPassword("hridesh");

		when(userDetailsService.getUserByToken("validToken")).thenReturn(user);

		mockMvc.perform(get("/user/profile").param("jwt", "validToken")).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("hridesh"));

		when(userDetailsService.getUserByToken("invalidToken")).thenReturn(null);

		mockMvc.perform(get("/user/profile").param("jwt", "invalidToken")).andExpect(status().isBadRequest());
	}

	@Test
	void testCreateUser() throws Exception {
		UserDTO user = new UserDTO();
		user.setUsername("newUser");
		user.setPassword("newUser");

		mockMvc.perform(
				post("/user").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.username").value("newUser"));
	}

	// @Test
	void testCreateAuthenticationToken() throws Exception {
		AuthRequest authRequest = new AuthRequest();
		authRequest.setUsername("testUser");
		authRequest.setPassword("testPass");

		UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("testUser")
				.password("testPass").authorities("USER").build();

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
		when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);
		when(jwtUtil.generateToken(userDetails)).thenReturn("jwtToken");

		mockMvc.perform(post("/user/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.jwt").value("jwtToken"));

		doThrow(new RuntimeException("Incorrect username or password")).when(authenticationManager)
				.authenticate(any(UsernamePasswordAuthenticationToken.class));

		mockMvc.perform(post("/user/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authRequest)))
				.andExpect(status().isInternalServerError());
	}
}
