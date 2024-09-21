package com.mckinsey.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mckinsey.dtos.ExchangeRateResponse;
import com.mckinsey.dtos.Item;

@Service
public class BillingService {

	private final DiscountService discountService;
	private final CurrencyExchangeService currencyExchangeService;
	
	Logger logger = LogManager.getLogger(BillingService.class);

	public BillingService(DiscountService discountService, CurrencyExchangeService currencyExchangeService) {
		this.discountService = discountService;
		this.currencyExchangeService = currencyExchangeService;
	}

	public double getDiscountedAmount(List<Item> items, String userType, int tenure) {

		ResponseEntity<Map<String, Double>> discountResponse = discountService.getDiscountedAmount(items, userType,
				tenure);

		if (discountResponse.getStatusCode() == HttpStatus.OK) {
			return discountResponse.getBody().get("disCountedAmount");
		}

		return items.stream().map(item -> item.getPrice()).collect(Collectors.summingDouble(v -> v));
	}
	
	@Cacheable(value = "getExchangeCurrancyRate", key = "#currency")
	public double getExchangeCurrancyRate(String currency, String targetCurrency) {
	    logger.info("Getting currency exchange rate, original currency: {}, target currency: {}", currency, targetCurrency);
	    int maxRetries = 3;
	    int attempt = 0;
	    while (attempt < maxRetries) {
	        ResponseEntity<ExchangeRateResponse> response = currencyExchangeService.getExchangeRates(currency);
	        if (response.getStatusCode() == HttpStatus.OK) {
	            Map<String, Double> rates = response.getBody().getConversionRates();
	            logger.info("Got currency exchange rate, original currency: {}, target currency: {}, rate: {}", currency, targetCurrency, rates.get(targetCurrency));
	            return rates.get(targetCurrency);
	        } else {
	            logger.error("Failed: Fetching exchange rate from exchange service, attempt: {}", attempt + 1);
	        }
	        attempt++;
	    }
	    logger.error("Failed: Fetching exchange rate from exchange service after {} attempts", maxRetries);
	    return 0;
	}


}
