package com.mckinsey.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mckinsey.dtos.BillingRequest;
import com.mckinsey.dtos.Item;
import com.mckinsey.dtos.UserDTO;
import com.mckinsey.services.AuthenticationService;
import com.mckinsey.services.BillingService;
import com.mckinsey.services.CurrencyExchangeService;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(BillingCountroller.class)
class BillingCountrollerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BillingService billingService;

	@MockBean
	private CurrencyExchangeService currencyExchangeService;

	@MockBean
	private AuthenticationService authenticationService;

	@InjectMocks
	private BillingCountroller billingCountroller;

	@Mock
	private HttpServletRequest request;

	@MockBean
	SecurityFilterChain securityFilterChain;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);

		// Mock the authentication service
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername("testuser");
		userDTO.setPassword("password");

		when(authenticationService.getUserByToken("valid-token"))
				.thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));

		UserDetails userDetails = new User(userDTO.getUsername(), userDTO.getPassword(), Collections.emptyList());
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		when(request.getRemoteAddr()).thenReturn("127.0.0.1");
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Test
	@WithMockUser
	void testGetBilledAmount() throws Exception {
		BillingRequest billRequest = new BillingRequest();
		billRequest.setItems(Arrays.asList(new Item()));
		billRequest.setOriginalCurrency("USD");
		billRequest.setTargetCurrency("EUR");
		billRequest.setUserType("employee");
		billRequest.setTenure(5);

		when(billingService.getDiscountedAmount(billRequest.getItems(), billRequest.getUserType(),
				billRequest.getTenure())).thenReturn(100.0);

		when(billingService.getExchangeCurrancyRate("USD", "EUR")).thenReturn(0.85);

		mockMvc.perform(post("/api/calculate").header("Authorization", "Bearer valid-token")
				.contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(billRequest)))
				.andExpect(status().isOk()).andExpect(content().string("Your bill amount(EUR): 85.0"));
	}
}
