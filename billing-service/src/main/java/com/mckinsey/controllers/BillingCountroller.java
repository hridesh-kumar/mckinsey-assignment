package com.mckinsey.controllers;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mckinsey.dtos.BillingRequest;
import com.mckinsey.services.BillingService;

@RestController
@RequestMapping("/api/calculate")
public class BillingCountroller {

	Logger logger = LogManager.getLogger(BillingCountroller.class);

	private final BillingService billingService;

	public BillingCountroller(BillingService billingService) {
		this.billingService = billingService;
	}

	@PostMapping
	public ResponseEntity<String> getBilledAmount(@RequestBody BillingRequest request) {

		logger.info("Billing for Items: {}, original Currency: {} to target currency: {}", Arrays.toString(request.getItems().toArray()), request.getOriginalCurrency(),
				request.getTargetCurrency());

		double discountedAmount = billingService.getDiscountedAmount(request.getItems(), request.getUserType(),
				request.getTenure());

		logger.info("Billed for Items: {}, original Currency: {} to target currency: {}", Arrays.toString(request.getItems().toArray()), request.getOriginalCurrency(),
				request.getTargetCurrency());

		logger.info("After discount amount: {}", discountedAmount);
		
		
		double targetCurrencyRate = billingService.getExchangeCurrancyRate(request.getOriginalCurrency(),
				request.getTargetCurrency());
		
		String response = "Your bill amount(USD): " + discountedAmount;
		if (targetCurrencyRate != 0) { 
			double amountInConvertedCurrency = discountedAmount * targetCurrencyRate;
			response = "Your bill amount(" + request.getTargetCurrency() + "): " + amountInConvertedCurrency;
		}

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

}
