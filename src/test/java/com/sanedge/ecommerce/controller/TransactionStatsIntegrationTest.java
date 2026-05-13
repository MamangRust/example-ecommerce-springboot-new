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

public class TransactionStatsIntegrationTest extends BaseIntegrationTest {

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
    void testAllTransactionStatsControllers() throws Exception {
        // 1. Monthly Amount Success
        mockMvc.perform(get("/api/transaction/monthly-success")
                .header("Authorization", "Bearer " + authToken)
                .param("month", "5")
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 2. Yearly Amount Success
        mockMvc.perform(get("/api/transaction/yearly-success")
                .header("Authorization", "Bearer " + authToken)
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 3. Monthly Amount Failed
        mockMvc.perform(get("/api/transaction/monthly-failed")
                .header("Authorization", "Bearer " + authToken)
                .param("month", "5")
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 4. Yearly Amount Failed
        mockMvc.perform(get("/api/transaction/yearly-failed")
                .header("Authorization", "Bearer " + authToken)
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 5. Merchant Monthly Amount Success
        mockMvc.perform(get("/api/transaction/merchant/monthly-success")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 6. Merchant Yearly Amount Success
        mockMvc.perform(get("/api/transaction/merchant/yearly-success")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 7. Merchant Monthly Amount Failed
        mockMvc.perform(get("/api/transaction/merchant/monthly-failed")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 8. Merchant Yearly Amount Failed
        mockMvc.perform(get("/api/transaction/merchant/yearly-failed")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 9. Monthly Method Success
        mockMvc.perform(get("/api/transaction/monthly-method-success")
                .header("Authorization", "Bearer " + authToken)
                .param("month", "5")
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 10. Yearly Method Success
        mockMvc.perform(get("/api/transaction/yearly-method-success")
                .header("Authorization", "Bearer " + authToken)
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 11. Monthly Method Failed
        mockMvc.perform(get("/api/transaction/monthly-method-failed")
                .header("Authorization", "Bearer " + authToken)
                .param("month", "5")
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 12. Yearly Method Failed
        mockMvc.perform(get("/api/transaction/yearly-method-failed")
                .header("Authorization", "Bearer " + authToken)
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 13. Merchant Monthly Method Success
        mockMvc.perform(get("/api/transaction/merchant/monthly-method-success")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 14. Merchant Yearly Method Success
        mockMvc.perform(get("/api/transaction/merchant/yearly-method-success")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 15. Merchant Monthly Method Failed
        mockMvc.perform(get("/api/transaction/merchant/monthly-method-failed")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 16. Merchant Yearly Method Failed
        mockMvc.perform(get("/api/transaction/merchant/yearly-method-failed")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
