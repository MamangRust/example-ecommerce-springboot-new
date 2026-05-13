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
import com.sanedge.ecommerce.domain.requests.merchantsociallink.CreateMerchantSocialRequest;
import com.sanedge.ecommerce.domain.requests.merchantsociallink.UpdateMerchantSocialRequest;
import com.sanedge.ecommerce.enums.Status;
import com.sanedge.ecommerce.models.merchant.Merchant;
import com.sanedge.ecommerce.models.merchant.MerchantDetail;
import com.sanedge.ecommerce.models.merchant.MerchantSocialMediaLink;
import com.sanedge.ecommerce.repository.merchant.MerchantCommandRepository;
import com.sanedge.ecommerce.repository.merchantdetail.MerchantDetailCommandRepository;
import com.sanedge.ecommerce.repository.merchantsociallink.MerchantSocialMediaLinkRepository;
import com.sanedge.ecommerce.security.JwtProvider;

public class MerchantSocialLinkControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @Autowired
    private MerchantDetailCommandRepository merchantDetailCommandRepository;

    @Autowired
    private MerchantSocialMediaLinkRepository merchantSocialMediaLinkRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;
    private Merchant merchant;
    private MerchantDetail detail;

    @BeforeEach
    void setupAuth() {
        this.authToken = jwtProvider.generateAccessToken(adminUser.getUsername());

        Merchant m = new Merchant();
        m.setUserId(adminUser.getUserId().intValue());
        m.setName("SocialMerchant");
        m.setDescription("Merchant for social tests");
        m.setAddress("Social Address");
        m.setContactEmail("social@test.com");
        m.setContactPhone("12345");
        m.setStatus(Status.SUCCESS);
        this.merchant = merchantCommandRepository.save(m);

        MerchantDetail d = new MerchantDetail();
        d.setMerchantId(merchant.getMerchantId().intValue());
        d.setDisplayName("Social Merchant Detail");
        d.setShortDescription("Short description");
        d.setWebsiteUrl("https://social-merchant.com");
        d.setCoverImageUrl("cover.jpg");
        d.setLogoUrl("logo.jpg");
        this.detail = merchantDetailCommandRepository.save(d);
    }

    @Test
    void shouldPerformAllMerchantSocialLinkEndpoints() throws Exception {
        // 1. Create
        CreateMerchantSocialRequest req = new CreateMerchantSocialRequest();
        req.setMerchantDetailId(detail.getMerchantDetailId().intValue());
        req.setPlatform("Instagram");
        req.setUrl("https://instagram.com/myshop");

        mockMvc.perform(post("/merchant-social-link/create")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        List<MerchantSocialMediaLink> links = merchantSocialMediaLinkRepository.findAll();
        assertThat(links).isNotEmpty();
        MerchantSocialMediaLink savedLink = links.get(links.size() - 1);
        Integer id = savedLink.getMerchantSocialId().intValue();

        // 2. Update
        UpdateMerchantSocialRequest updateReq = new UpdateMerchantSocialRequest();
        updateReq.setId(id);
        updateReq.setMerchantDetailId(detail.getMerchantDetailId().intValue());
        updateReq.setPlatform("Facebook");
        updateReq.setUrl("https://facebook.com/myshop");

        mockMvc.perform(post("/merchant-social-link/update/" + id)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 3. Trash
        mockMvc.perform(post("/merchant-social-link/trash/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 4. Restore
        mockMvc.perform(post("/merchant-social-link/restore/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Trash again for deletion tests
        mockMvc.perform(post("/merchant-social-link/trash/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 5. Permanent Delete
        mockMvc.perform(delete("/merchant-social-link/delete/" + id)
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 6. Restore All
        mockMvc.perform(post("/merchant-social-link/restore/all")
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 7. Delete All (permanent)
        mockMvc.perform(post("/merchant-social-link/delete/all")
                .header("Authorization", "Bearer " + authToken)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
