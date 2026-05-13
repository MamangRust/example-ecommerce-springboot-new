package com.sanedge.ecommerce.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.category.Category;
import com.sanedge.ecommerce.repository.category.CategoryCommandRepository;
import com.sanedge.ecommerce.security.JwtProvider;

public class CategoryStatsIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CategoryCommandRepository categoryCommandRepository;

    private String authToken;
    private Long categoryId;

    @BeforeEach
    void setupAuth() {
        this.authToken = jwtProvider.generateAccessToken(adminUser.getUsername());

        Category cat = new Category();
        cat.setName("Stats Category");
        cat.setSlugCategory("stats-category");
        cat.setDescription("Category for stats test");
        cat.setImageCategory("image.png");
        cat = categoryCommandRepository.save(cat);
        this.categoryId = cat.getCategoryId();

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testAllCategoryPriceAndTotalPriceStatsControllers() throws Exception {
        // 1. Monthly Pricing
        mockMvc.perform(get("/api/category/monthly-pricing")
                .header("Authorization", "Bearer " + authToken)
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 2. Yearly Pricing
        mockMvc.perform(get("/api/category/yearly-pricing")
                .header("Authorization", "Bearer " + authToken)
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 3. Monthly Total Pricing
        mockMvc.perform(get("/api/category/monthly-total-pricing")
                .header("Authorization", "Bearer " + authToken)
                .param("month", "5")
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 4. Yearly Total Pricing
        mockMvc.perform(get("/api/category/yearly-total-pricing")
                .header("Authorization", "Bearer " + authToken)
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 5. Merchant Monthly Pricing
        mockMvc.perform(get("/api/category/merchant/monthly-pricing")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 6. Merchant Yearly Pricing
        mockMvc.perform(get("/api/category/merchant/yearly-pricing")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 7. Merchant Monthly Total Pricing
        mockMvc.perform(get("/api/category/merchant/monthly-total-pricing")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 8. Merchant Yearly Total Pricing
        mockMvc.perform(get("/api/category/merchant/yearly-total-pricing")
                .header("Authorization", "Bearer " + authToken)
                .param("merchantId", adminMerchant.getMerchantId().toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 9. MyCategory Monthly Pricing
        mockMvc.perform(get("/api/category/mycategory/monthly-pricing")
                .header("Authorization", "Bearer " + authToken)
                .param("categoryId", categoryId.toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 10. MyCategory Yearly Pricing
        mockMvc.perform(get("/api/category/mycategory/yearly-pricing")
                .header("Authorization", "Bearer " + authToken)
                .param("categoryId", categoryId.toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 11. MyCategory Monthly Total Pricing
        mockMvc.perform(get("/api/category/mycategory/monthly-total-pricing")
                .header("Authorization", "Bearer " + authToken)
                .param("categoryId", categoryId.toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 12. MyCategory Yearly Total Pricing
        mockMvc.perform(get("/api/category/mycategory/yearly-total-pricing")
                .header("Authorization", "Bearer " + authToken)
                .param("categoryId", categoryId.toString())
                .param("year", "2026")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
