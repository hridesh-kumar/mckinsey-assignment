package com.mckinsey.dtos;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateResponse {

	private String result;
    private String documentation;
    
    @JsonAlias(value = "terms_of_use")
    private String termsOfUse;
    
    @JsonAlias(value = "time_last_update_unix")
    private long timeLastUpdateUnix;
    
    @JsonAlias(value = "time_last_update_utc")
    private String timeLastUpdateUtc;
    
    @JsonAlias(value = "time_next_update_unix")
    private long timeNextUpdateUnix;
    
    @JsonAlias(value = "time_next_update_utc")
    private String timeNextUpdateUtc;
    
    @JsonAlias(value = "base_code")
    private String baseCode;
    
    @JsonAlias(value = "conversion_rates")
    private Map<String, Double> conversionRates;
	
}
