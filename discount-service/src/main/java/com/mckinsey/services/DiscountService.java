package com.mckinsey.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mckinsey.discounthandler.AffiliateDiscountHandler;
import com.mckinsey.discounthandler.CustomerDiscountHandler;
import com.mckinsey.discounthandler.DiscountHandler;
import com.mckinsey.discounthandler.EmployeeDiscountHandler;
import com.mckinsey.discounthandler.HundredDollarDiscountHandler;
import com.mckinsey.dtos.Item;

@Service
public class DiscountService {

	Logger logger = LoggerFactory.getLogger(DiscountService.class);

	public double calculateDiscountedAmount(List<Item> items, String userType, int tenure) {

		DiscountHandler employeeDiscount = new EmployeeDiscountHandler();
		DiscountHandler affinityDiscount = new AffiliateDiscountHandler();
		DiscountHandler customerDiscount = new CustomerDiscountHandler(tenure);
		DiscountHandler hunDiscountHandler = new HundredDollarDiscountHandler();

		hunDiscountHandler.setNextHandler(employeeDiscount);
		employeeDiscount.setNextHandler(affinityDiscount);
		affinityDiscount.setNextHandler(customerDiscount);
		customerDiscount.setNextHandler(null);

		double totalAmount = items.stream().map(item -> item.getPrice()).collect(Collectors.summingDouble(v -> v));

		double totalDiscount = hunDiscountHandler.applyDiscount(items, userType);
		logger.info("Total discount: {}", totalDiscount);
		return totalAmount - totalDiscount;

	}
}
