package com.mckinsey.dtos;


import java.util.List;

import lombok.Data;

@Data
public class BillingRequest {

	private List<Item> items;
	private String userType;
	private int tenure;
	private String originalCurrency;
	private String targetCurrency;
}
