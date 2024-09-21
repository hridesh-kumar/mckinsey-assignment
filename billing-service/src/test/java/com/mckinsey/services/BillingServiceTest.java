package com.mckinsey.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mckinsey.dtos.ExchangeRateResponse;
import com.mckinsey.dtos.Item;

class BillingServiceTest {

	@Mock
	private DiscountService discountService;

	@Mock
	private CurrencyExchangeService currencyExchangeService;

	@InjectMocks
	private BillingService billingService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetDiscountedAmount() {
		List<Item> items = Arrays.asList(new Item("item1", "", 50.0), new Item("item2", "", 50.0));
		String userType = "employee";
		int tenure = 5;

		when(discountService.getDiscountedAmount(items, userType, tenure))
				.thenReturn(new ResponseEntity<>(Map.of("disCountedAmount", 80.0), HttpStatus.OK));
		
		when(discountService.getDiscountedAmount(items, "none", 0))
		.thenReturn(new ResponseEntity<>(Map.of("disCountedAmount", 100.0), HttpStatus.BAD_REQUEST));

		double discountedAmount = billingService.getDiscountedAmount(items, userType, tenure);
		assertEquals(80.0, discountedAmount);
		
		double defaultDiscountedAmount = billingService.getDiscountedAmount(items, "none", 0);
		assertEquals(100.0, defaultDiscountedAmount);
	}

	@Test
	void testGetDiscountedAmountWithoutDiscount() {
		List<Item> items = Arrays.asList(new Item("item1", "", 50.0), new Item("item2", "", 50.0));
		String userType = "employee";
		int tenure = 5;

		when(discountService.getDiscountedAmount(items, userType, tenure))
				.thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

		double discountedAmount = billingService.getDiscountedAmount(items, userType, tenure);
		assertEquals(100.0, discountedAmount);
	}

	@Test
	void testGetExchangeCurrancyRate() {
		String currency = "USD";
		String targetCurrency = "EUR";

		ExchangeRateResponse exchangeResponse = new ExchangeRateResponse();
		exchangeResponse.setConversionRates(Map.of("EUR", 0.85));
		ResponseEntity<ExchangeRateResponse> res = new ResponseEntity<ExchangeRateResponse>(exchangeResponse,
				HttpStatus.OK);

		when(currencyExchangeService.getExchangeRates(currency)).thenReturn(res);

		double exchangeRate = billingService.getExchangeCurrancyRate(currency, targetCurrency);
		assertEquals(0.85, exchangeRate);
	}

	@Test
	void testGetExchangeCurrancyRateWithRetries() {
		String currency = "USD";
		String targetCurrency = "EUR";

		ExchangeRateResponse exchangeResponse = new ExchangeRateResponse();
		exchangeResponse.setConversionRates(Map.of("EUR", 0.85));
		ResponseEntity<ExchangeRateResponse> res = new ResponseEntity<ExchangeRateResponse>(exchangeResponse,
				HttpStatus.OK);

		when(currencyExchangeService.getExchangeRates(currency))
				.thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST))
				.thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST)).thenReturn(res);

		double exchangeRate = billingService.getExchangeCurrancyRate(currency, targetCurrency);
		assertEquals(0.85, exchangeRate);
	}

	@Test
	void testGetExchangeCurrancyRateFailure() {
		String currency = "USD";
		String targetCurrency = "EUR";

		when(currencyExchangeService.getExchangeRates(currency))
				.thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

		double exchangeRate = billingService.getExchangeCurrancyRate(currency, targetCurrency);
		assertEquals(0.0, exchangeRate);
	}
}
