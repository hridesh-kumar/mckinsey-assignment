package com.mckinsey.discounthandler;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mckinsey.dtos.Item;

public class AffiliateDiscountHandler implements DiscountHandler{

	Logger logger = LoggerFactory.getLogger(AffiliateDiscountHandler.class);
	
	

	private DiscountHandler nextHandler;

    @Override
    public void setNextHandler(DiscountHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
	public double applyDiscount(List<Item> items, String role) {
		double totalDiscount = 0;
		if ("affiliate".equals(role)) {
			logger.info("Calcuating: Affiliate discount");
			totalDiscount = items.stream().filter(item -> !item.getCategory().equals("grocery")).map(item -> {
				logger.info("Discount on item: {}, discount: {}",item.getName(), item.getPrice() * 0.10);
				return item.getPrice() * 0.10;
			}).collect(Collectors.summingDouble(v -> v));
			logger.info("Affiliate total discount: {}",totalDiscount);
			return totalDiscount;
		} else if (nextHandler != null) {
			return totalDiscount + nextHandler.applyDiscount(items, role);
		}
		return totalDiscount;

    }
}
