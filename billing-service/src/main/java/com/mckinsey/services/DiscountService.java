package com.mckinsey.services;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.mckinsey.dtos.Item;

@FeignClient(name = "DiscountService", url = "${app.discount-service.url}", path = "/discount")
@Lazy
public interface DiscountService {

	@PostMapping
	public ResponseEntity<Map<String, Double>> getDiscountedAmount(@RequestBody List<Item> items, @RequestParam String userType, @RequestParam int tenure);
}
