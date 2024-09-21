package com.mckinsey.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.McKinsey.Controllers.DiscountController;
import com.McKinsey.dtos.Item;
import com.McKinsey.services.DiscountService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class DiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private DiscountService discountService;

    @InjectMocks
    private DiscountController discountController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetDiscountedAmount() throws Exception {
        List<Item> items = Arrays.asList(new Item("item1", "",100.0), new Item("item2", "", 200.0));
        String userType = "regular";
        int tenure = 5;
        double discountedAmount = 270.0;

        when(discountService.calculateDiscountedAmount(items, userType, tenure)).thenReturn(discountedAmount);

        mockMvc.perform(post("/discount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(items))
                .param("userType", userType)
                .param("tenure", String.valueOf(tenure)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disCountedAmount").value(discountedAmount));
    }
}
