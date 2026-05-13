package com.sanedge.ecommerce.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.security.JwtProvider;

public class OrderStatsIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    private String authToken;

    @BeforeEach
    void setupAuth() {
        this.authToken = jwtProvider.generateAccessToken(adminUser.getUsername());
    }

    @Test
    void testAllOrderStatsControllers() throws Exception {
        // 1. Monthly Revenue
        mockMvc.perform(get("/api/order/monthly-revenue")
                .header("Authorization", "Bearer " + authToken)
                .param("yearMonth", "202605")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 2. Yearly Revenue
        mockMvc.perform(get("/api/order/yearly-revenue")
                .header("Authorization", "Bearer " + authToken)
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 3. Monthly Total Revenue
        mockMvc.perform(get("/api/order/monthly-total-revenue")
                .header("Authorization", "Bearer " + authToken)
                .param("month", "5")
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 4. Yearly Total Revenue
        mockMvc.perform(get("/api/order/yearly-total-revenue")
                .header("Authorization", "Bearer " + authToken)
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 5. Merchant Monthly Revenue
        mockMvc.perform(get("/api/order/merchant/monthly-revenue")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 6. Merchant Yearly Revenue
        mockMvc.perform(get("/api/order/merchant/yearly-revenue")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 7. Merchant Monthly Total Revenue
        mockMvc.perform(get("/api/order/merchant/monthly-total-revenue")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 8. Merchant Yearly Total Revenue
        mockMvc.perform(get("/api/order/merchant/yearly-total-revenue")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
