package com.mckinsey.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mckinsey.dtos.ExchangeRateResponse;

@FeignClient(name = "CurrencyExchangeService", url = "${currency.exchange.service.uri}", path = "/latest")
public interface CurrencyExchangeService {

	@GetMapping("/{currency}")
	ResponseEntity<ExchangeRateResponse> getExchangeRates(@PathVariable String currency);
}
