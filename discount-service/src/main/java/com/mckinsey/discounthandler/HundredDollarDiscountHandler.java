package com.mckinsey.discounthandler;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mckinsey.dtos.Item;

public class HundredDollarDiscountHandler implements DiscountHandler {

	Logger logger = LoggerFactory.getLogger(HundredDollarDiscountHandler.class);

	private DiscountHandler nextHandler;

	@Override
	public void setNextHandler(DiscountHandler nextHandler) {
		this.nextHandler = nextHandler;
	}

	@Override
	public double applyDiscount(List<Item> items, String role) {

		double totalAmount = items.stream().map(item -> item.getPrice()).collect(Collectors.summingDouble(v -> v));

		double discount = (totalAmount / 100) * 5;

		logger.info("Every Hundred total discount: {}", discount);

		// if not groceries apply percent discount
		if (nextHandler != null) {
			discount = discount + nextHandler.applyDiscount(items, role);
		}
		return discount;
	}
}
