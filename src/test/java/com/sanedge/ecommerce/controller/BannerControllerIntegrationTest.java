package com.sanedge.ecommerce.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.domain.requests.banner.CreateBannerRequest;
import com.sanedge.ecommerce.security.JwtProvider;

public class BannerControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setupAuth() {
        this.authToken = jwtProvider.generateAccessToken(adminUser.getUsername());
    }

    @Test
    void shouldFindAllAndCreateBanner() throws Exception {
        mockMvc.perform(get("/api/banner")
                .header("Authorization", "Bearer " + authToken)
                .param("search", "Promo")
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        CreateBannerRequest req = new CreateBannerRequest();
        req.setName("Controller Banner");
        req.setStartDate("2026-05-01");
        req.setEndDate("2026-05-31");
        req.setStartTime("10:00");
        req.setEndTime("22:00");
        req.setIsActive(true);

        mockMvc.perform(post("/api/banner/create")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
