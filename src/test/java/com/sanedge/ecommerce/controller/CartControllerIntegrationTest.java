package com.sanedge.ecommerce.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.domain.requests.cart.CreateCartRequest;
import com.sanedge.ecommerce.domain.requests.cart.DeleteCartRequest;
import com.sanedge.ecommerce.models.Cart;
import com.sanedge.ecommerce.repository.cart.CartCommandRepository;
import com.sanedge.ecommerce.security.JwtProvider;

public class CartControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CartCommandRepository cartCommandRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setupAuth() {
        this.authToken = jwtProvider.generateAccessToken(adminUser.getUsername());
    }

    @Test
    void shouldFindAllCarts() throws Exception {
        Cart cart = new Cart();
        cart.setUserId(regularUser.getUserId().intValue());
        cart.setProductId(111);
        cart.setName("Super Keyboard");
        cart.setPrice(500);
        cart.setImage("kb.png");
        cart.setQuantity(1);
        cart.setWeight(100);
        cartCommandRepository.save(cart);

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/cart")
                .header("Authorization", "Bearer " + authToken)
                .param("userId", String.valueOf(regularUser.getUserId()))
                .param("search", "Keyboard")
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateCartThenDelete() throws Exception {
        CreateCartRequest req = new CreateCartRequest();
        req.setUserId(regularUser.getUserId().intValue());
        req.setProductId(222);
        req.setQuantity(4);

        String responseStr = mockMvc.perform(post("/api/cart/create")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(responseStr).contains("success");

        // Now test delete endpoint
        mockMvc.perform(delete("/api/cart/1234567")
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
