package com.McKinsey.discounthandler;

import java.util.List;

import com.McKinsey.dtos.Item;



public interface DiscountHandler {

	void setNextHandler(DiscountHandler nextHandler);

	double applyDiscount(List<Item> items, String role);
}
