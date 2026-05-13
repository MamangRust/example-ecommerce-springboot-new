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
import com.sanedge.ecommerce.domain.requests.merchantbusiness.CreateMerchantBusinessRequest;
import com.sanedge.ecommerce.domain.requests.merchantbusiness.UpdateMerchantBusinessRequest;
import com.sanedge.ecommerce.enums.Status;
import com.sanedge.ecommerce.models.merchant.Merchant;
import com.sanedge.ecommerce.models.merchant.MerchantBusinessInformation;
import com.sanedge.ecommerce.repository.merchant.MerchantCommandRepository;
import com.sanedge.ecommerce.repository.merchantbusiness.MerchantBusinessCommandRepository;
import com.sanedge.ecommerce.security.JwtProvider;

public class MerchantBusinessControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @Autowired
    private MerchantBusinessCommandRepository merchantBusinessCommandRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;
    private Merchant merchant;

    @BeforeEach
    void setupAuth() {
        this.authToken = jwtProvider.generateAccessToken(adminUser.getUsername());

        Merchant m = new Merchant();
        m.setUserId(adminUser.getUserId().intValue());
        m.setName("BusinessMerchant");
        m.setDescription("Merchant for business tests");
        m.setAddress("Business Address");
        m.setContactEmail("biz@test.com");
        m.setContactPhone("12345");
        m.setStatus(Status.SUCCESS);
        this.merchant = merchantCommandRepository.save(m);
    }

    @Test
    void shouldPerformAllMerchantBusinessEndpoints() throws Exception {
        // 1. Create
        CreateMerchantBusinessRequest req = new CreateMerchantBusinessRequest();
        req.setMerchantId(merchant.getMerchantId().intValue());
        req.setBusinessType("Retail");
        req.setTaxId("123-456-789");
        req.setEstablishedYear(2020);
        req.setNumberOfEmployees(50);
        req.setWebsiteUrl("https://retail.com");

        mockMvc.perform(post("/api/merchant-business/create")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<MerchantBusinessInformation> infos = merchantBusinessCommandRepository.findAll();
        assertThat(infos).isNotEmpty();
        MerchantBusinessInformation savedInfo = infos.get(infos.size() - 1);
        Integer id = savedInfo.getMerchantBusinessInfoId().intValue();

        // 2. Find All
        mockMvc.perform(get("/api/merchant-business")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 3. Find By ID
        mockMvc.perform(get("/api/merchant-business/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 4. Find Active
        mockMvc.perform(get("/api/merchant-business/active")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 5. Find Trashed
        mockMvc.perform(get("/api/merchant-business/trashed")
                .header("Authorization", "Bearer " + authToken)
                .param("page", "1")
                .param("pageSize", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 6. Update
        UpdateMerchantBusinessRequest updateReq = new UpdateMerchantBusinessRequest();
        updateReq.setMerchantBusinessInfoId(null); // Must be @Null in the request payload
        updateReq.setBusinessType("E-commerce");
        updateReq.setTaxId("987-654-321");
        updateReq.setEstablishedYear(2021);
        updateReq.setNumberOfEmployees(60);
        updateReq.setWebsiteUrl("https://ecommerce-biz.com");

        mockMvc.perform(post("/api/merchant-business/update/" + id)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 7. Trash
        mockMvc.perform(post("/api/merchant-business/trashed/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 8. Restore
        mockMvc.perform(post("/api/merchant-business/restore/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Trash again for deletion tests
        mockMvc.perform(post("/api/merchant-business/trashed/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 9. Permanent Delete
        mockMvc.perform(delete("/api/merchant-business/permanent/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 10. Restore All
        mockMvc.perform(post("/api/merchant-business/restore/all")
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 11. Delete All (permanent)
        mockMvc.perform(post("/api/merchant-business/permanent/all")
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
