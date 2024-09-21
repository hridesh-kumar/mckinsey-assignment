package com.McKinsey.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.McKinsey.dtos.Item;
import com.McKinsey.services.DiscountService;

@RestController
@RequestMapping("/discount")
public class DiscountController {

	private final DiscountService discountService;

	public DiscountController(DiscountService discountService) {
		this.discountService = discountService;
	}

	@PostMapping
	public ResponseEntity<Map<String, Double>> getDiscountedAmount(@RequestBody List<Item> items,
			@RequestParam String userType, @RequestParam int tenure) {
		Map<String, Double> response = new HashMap<String, Double>();

		double disCountedAmount = discountService.calculateDiscountedAmount(items, userType, tenure);

		response.put("disCountedAmount", disCountedAmount);
		return new ResponseEntity<Map<String, Double>>(response, HttpStatus.OK);
	}

}
