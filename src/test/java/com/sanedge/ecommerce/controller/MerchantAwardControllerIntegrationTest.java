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
import com.sanedge.ecommerce.domain.requests.merchantawrd.CreateMerchantAwardRequest;
import com.sanedge.ecommerce.domain.requests.merchantawrd.UpdateMerchantAwardRequest;
import com.sanedge.ecommerce.enums.Status;
import com.sanedge.ecommerce.models.merchant.Merchant;
import com.sanedge.ecommerce.models.merchant.MerchantCertificationAndAward;
import com.sanedge.ecommerce.repository.merchant.MerchantCommandRepository;
import com.sanedge.ecommerce.repository.merchantaward.MerchantAwardCommandRepository;
import com.sanedge.ecommerce.security.JwtProvider;

public class MerchantAwardControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @Autowired
    private MerchantAwardCommandRepository merchantAwardCommandRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;
    private Merchant merchant;

    @BeforeEach
    void setupAuth() {
        this.authToken = jwtProvider.generateAccessToken(adminUser.getUsername());

        Merchant m = new Merchant();
        m.setUserId(adminUser.getUserId().intValue());
        m.setName("AwardMerchant");
        m.setDescription("Merchant for award tests");
        m.setAddress("Award Address");
        m.setContactEmail("award@test.com");
        m.setContactPhone("12345");
        m.setStatus(Status.SUCCESS);
        this.merchant = merchantCommandRepository.save(m);
    }

    @Test
    void shouldPerformAllMerchantAwardEndpoints() throws Exception {
        // 1. Create
        CreateMerchantAwardRequest req = new CreateMerchantAwardRequest();
        req.setMerchantId(merchant.getMerchantId().intValue());
        req.setTitle("ISO Certification");
        req.setDescription("Award for quality");
        req.setIssuedBy("ISO Org");
        req.setIssueDate("2024-01-01");
        req.setExpiryDate("2025-01-01");
        req.setCertificateUrl("https://iso.org");

        mockMvc.perform(post("/merchant-award/create")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<MerchantCertificationAndAward> awards = merchantAwardCommandRepository.findAll();
        assertThat(awards).isNotEmpty();
        MerchantCertificationAndAward savedAward = awards.get(awards.size() - 1);
        Integer id = savedAward.getMerchantCertificationId().intValue();

        // 2. Find All
        mockMvc.perform(get("/merchant-award")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 3. Find By ID
        mockMvc.perform(get("/merchant-award/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 4. Find Active
        mockMvc.perform(get("/merchant-award/active")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 5. Find Trashed
        mockMvc.perform(get("/merchant-award/trashed")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 6. Update
        UpdateMerchantAwardRequest updateReq = new UpdateMerchantAwardRequest();
        updateReq.setMerchantCertificationId(id);
        updateReq.setTitle("ISO 9001");
        updateReq.setDescription("Updated description");
        updateReq.setIssuedBy("ISO Organization");
        updateReq.setIssueDate("2024-02-01");
        updateReq.setExpiryDate("2025-02-01");
        updateReq.setCertificateUrl("https://iso9001.org");

        mockMvc.perform(post("/merchant-award/update/" + id)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 7. Trash
        mockMvc.perform(post("/merchant-award/trashed/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 8. Restore
        mockMvc.perform(post("/merchant-award/restore/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Trash again for deletion tests
        mockMvc.perform(post("/merchant-award/trashed/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 9. Permanent Delete
        mockMvc.perform(delete("/merchant-award/permanent/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 10. Restore All
        mockMvc.perform(post("/merchant-award/restore/all")
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 11. Delete All (permanent)
        mockMvc.perform(post("/merchant-award/permanent/all")
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
