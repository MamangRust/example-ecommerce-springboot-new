package com.sanedge.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.domain.requests.merchantsociallink.CreateMerchantSocialRequest;
import com.sanedge.ecommerce.domain.requests.merchantsociallink.UpdateMerchantSocialRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.merchantsociallink.MerchantSocialMediaLinkResponse;
import com.sanedge.ecommerce.domain.responses.merchantsociallink.MerchantSocialMediaLinkResponseDeleteAt;
import com.sanedge.ecommerce.enums.Status;
import com.sanedge.ecommerce.models.merchant.Merchant;
import com.sanedge.ecommerce.models.merchant.MerchantDetail;
import com.sanedge.ecommerce.repository.merchant.MerchantCommandRepository;
import com.sanedge.ecommerce.repository.merchantdetail.MerchantDetailCommandRepository;

public class MerchantSocialLinkServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MerchantSocialLinkService service;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @Autowired
    private MerchantDetailCommandRepository merchantDetailCommandRepository;

    private Merchant merchant;
    private MerchantDetail detail;

    @BeforeEach
    void setup() {
        Merchant m = new Merchant();
        m.setUserId(adminUser.getUserId().intValue());
        m.setName("ServiceSocialMerchant");
        m.setDescription("Merchant for social tests");
        m.setAddress("Address");
        m.setContactEmail("service-social@test.com");
        m.setContactPhone("12345");
        m.setStatus(Status.SUCCESS);
        this.merchant = merchantCommandRepository.save(m);

        MerchantDetail d = new MerchantDetail();
        d.setMerchantId(merchant.getMerchantId().intValue());
        d.setDisplayName("Social Merchant Detail");
        d.setShortDescription("Short description");
        d.setWebsiteUrl("https://service-social-merchant.com");
        d.setCoverImageUrl("cover.jpg");
        d.setLogoUrl("logo.jpg");
        this.detail = merchantDetailCommandRepository.save(d);
    }

    @Test
    void testAllMerchantSocialLinkServiceMethods() {
        // 1. Create
        CreateMerchantSocialRequest req = new CreateMerchantSocialRequest();
        req.setMerchantDetailId(detail.getMerchantDetailId().intValue());
        req.setPlatform("Instagram");
        req.setUrl("https://instagram.com/awesome");

        ApiResponse<MerchantSocialMediaLinkResponse> createResp = service.create(req);
        assertThat(createResp.getStatus()).isEqualTo("success");
        Long id = createResp.getData().getId();

        entityManager.flush();
        entityManager.clear();

        // 2. Update
        UpdateMerchantSocialRequest updateReq = new UpdateMerchantSocialRequest();
        updateReq.setId(id.intValue());
        updateReq.setMerchantDetailId(detail.getMerchantDetailId().intValue());
        updateReq.setPlatform("Facebook");
        updateReq.setUrl("https://facebook.com/awesome");

        ApiResponse<MerchantSocialMediaLinkResponse> updateResp = service.update(updateReq);
        assertThat(updateResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 3. Trash
        ApiResponse<MerchantSocialMediaLinkResponseDeleteAt> trashResp = service.trash(id.intValue());
        assertThat(trashResp.getStatus()).isEqualTo("success");

        // 4. Restore
        ApiResponse<MerchantSocialMediaLinkResponseDeleteAt> restoreResp = service.restore(id.intValue());
        assertThat(restoreResp.getStatus()).isEqualTo("success");

        // Trash again before delete permanent so deleted_at is not null
        ApiResponse<MerchantSocialMediaLinkResponseDeleteAt> trashAgainResp = service.trash(id.intValue());
        assertThat(trashAgainResp.getStatus()).isEqualTo("success");

        // 5. Delete
        ApiResponse<Boolean> delResp = service.delete(id.intValue());
        assertThat(delResp.getStatus()).isEqualTo("success");

        // 6. Restore All
        ApiResponse<Boolean> restoreAllResp = service.restoreAll();
        assertThat(restoreAllResp.getStatus()).isEqualTo("success");

        // 7. Delete All
        ApiResponse<Boolean> delAllResp = service.deleteAll();
        assertThat(delAllResp.getStatus()).isEqualTo("success");
    }
}
