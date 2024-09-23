package com.mckinsey.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.mckinsey.dtos.Item;

class DiscountServiceTest {

	@InjectMocks
	private DiscountService discountService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCalculateDiscountedAmount() {
		List<Item> items = Arrays.asList(new Item("item1", "cloth", 100.0), new Item("item2", "grocery", 200.0));
		int tenure = 5;

		double empDiscountedAmount = discountService.calculateDiscountedAmount(items, "employee", tenure);

		assertEquals(255.0, empDiscountedAmount);

		double affiDiscountedAmount = discountService.calculateDiscountedAmount(items, "affiliate", tenure);

		assertEquals(275.0, affiDiscountedAmount);

		double regularDiscountedAmount = discountService.calculateDiscountedAmount(items, "regular", tenure);

		assertEquals(280.0, regularDiscountedAmount);

	}
}
