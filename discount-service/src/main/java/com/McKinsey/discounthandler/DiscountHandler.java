package com.mckinsey.discounthandler;

import java.util.List;

import com.mckinsey.dtos.Item;



public interface DiscountHandler {

	void setNextHandler(DiscountHandler nextHandler);

	double applyDiscount(List<Item> items, String role);
}
