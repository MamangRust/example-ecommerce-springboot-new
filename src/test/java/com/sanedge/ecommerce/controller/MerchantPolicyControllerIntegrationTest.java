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
import com.sanedge.ecommerce.domain.requests.merchantpolicy.CreateMerchantPolicyRequest;
import com.sanedge.ecommerce.domain.requests.merchantpolicy.UpdateMerchantPolicyRequest;
import com.sanedge.ecommerce.enums.Status;
import com.sanedge.ecommerce.models.merchant.Merchant;
import com.sanedge.ecommerce.models.merchant.MerchantPolicy;
import com.sanedge.ecommerce.repository.merchant.MerchantCommandRepository;
import com.sanedge.ecommerce.repository.merchantpolicy.MerchantPolicyCommandRepository;
import com.sanedge.ecommerce.security.JwtProvider;

public class MerchantPolicyControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @Autowired
    private MerchantPolicyCommandRepository merchantPolicyCommandRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;
    private Merchant merchant;

    @BeforeEach
    void setupAuth() {
        this.authToken = jwtProvider.generateAccessToken(adminUser.getUsername());

        Merchant m = new Merchant();
        m.setUserId(adminUser.getUserId().intValue());
        m.setName("PolicyMerchant");
        m.setDescription("Merchant for policy tests");
        m.setAddress("Policy Address");
        m.setContactEmail("policy@test.com");
        m.setContactPhone("12345");
        m.setStatus(Status.SUCCESS);
        this.merchant = merchantCommandRepository.save(m);
    }

    @Test
    void shouldPerformAllMerchantPolicyEndpoints() throws Exception {
        // 1. Create
        CreateMerchantPolicyRequest req = new CreateMerchantPolicyRequest();
        req.setMerchantId(merchant.getMerchantId().intValue());
        req.setPolicyType("Refund");
        req.setTitle("Refund Policy");
        req.setDescription("7-day refund policy");

        mockMvc.perform(post("/api/merchant-policy/create")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        List<MerchantPolicy> policies = merchantPolicyCommandRepository.findAll();
        assertThat(policies).isNotEmpty();
        MerchantPolicy savedPolicy = policies.get(policies.size() - 1);
        Integer id = savedPolicy.getMerchantPolicyId().intValue();

        // 2. Find All
        mockMvc.perform(get("/api/merchant-policy")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 3. Find By ID
        mockMvc.perform(get("/api/merchant-policy/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 4. Find Active
        mockMvc.perform(get("/api/merchant-policy/active")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 5. Find Trashed
        mockMvc.perform(get("/api/merchant-policy/trashed")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 6. Update
        UpdateMerchantPolicyRequest updateReq = new UpdateMerchantPolicyRequest();
        updateReq.setMerchantPolicyId(id);
        updateReq.setPolicyType("Replacement");
        updateReq.setTitle("Replacement Policy");
        updateReq.setDescription("14-day replacement policy");

        mockMvc.perform(post("/api/merchant-policy/update/" + id)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 7. Trash
        mockMvc.perform(post("/api/merchant-policy/trashed/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 8. Restore
        mockMvc.perform(post("/api/merchant-policy/restore/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Trash again for deletion tests
        mockMvc.perform(post("/api/merchant-policy/trashed/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 9. Permanent Delete
        mockMvc.perform(delete("/api/merchant-policy/permanent/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 10. Restore All
        mockMvc.perform(post("/api/merchant-policy/restore/all")
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 11. Delete All (permanent)
        mockMvc.perform(post("/api/merchant-policy/permanent/all")
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
