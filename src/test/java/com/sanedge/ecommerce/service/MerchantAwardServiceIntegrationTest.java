package com.sanedge.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.domain.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.domain.requests.merchantawrd.CreateMerchantAwardRequest;
import com.sanedge.ecommerce.domain.requests.merchantawrd.UpdateMerchantAwardRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.merchantaward.MerchantAwardResponse;
import com.sanedge.ecommerce.domain.responses.merchantaward.MerchantAwardResponseDeleteAt;
import com.sanedge.ecommerce.enums.Status;
import com.sanedge.ecommerce.models.merchant.Merchant;
import com.sanedge.ecommerce.repository.merchant.MerchantCommandRepository;
import com.sanedge.ecommerce.service.merchantaward.MerchantAwardCommandService;
import com.sanedge.ecommerce.service.merchantaward.MerchantAwardQueryService;

public class MerchantAwardServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MerchantAwardCommandService commandService;

    @Autowired
    private MerchantAwardQueryService queryService;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    private Merchant merchant;

    @BeforeEach
    void setup() {
        Merchant m = new Merchant();
        m.setUserId(adminUser.getUserId().intValue());
        m.setName("ServiceAwardMerchant");
        m.setDescription("Merchant for tests");
        m.setAddress("Address");
        m.setContactEmail("service-award@test.com");
        m.setContactPhone("12345");
        m.setStatus(Status.SUCCESS);
        this.merchant = merchantCommandRepository.save(m);
    }

    @Test
    void testAllMerchantAwardServiceMethods() {
        // 1. Create
        CreateMerchantAwardRequest req = new CreateMerchantAwardRequest();
        req.setMerchantId(merchant.getMerchantId().intValue());
        req.setTitle("ISO 9001");
        req.setDescription("Service test award");
        req.setIssuedBy("ISO Organization");
        req.setIssueDate("2024-01-01");
        req.setExpiryDate("2025-01-01");
        req.setCertificateUrl("https://service-award.com");

        ApiResponse<MerchantAwardResponse> createResp = commandService.createMerchantAward(req);
        assertThat(createResp.getStatus()).isEqualTo("success");
        Long id = createResp.getData().getId();

        entityManager.flush();
        entityManager.clear();

        // 2. Find All
        FindAllMerchantRequest findReq = new FindAllMerchantRequest();
        findReq.setSearch("ISO 9001");
        findReq.setPage(1);
        findReq.setPageSize(10);

        ApiResponsePagination<List<MerchantAwardResponse>> listResp = queryService.findAll(findReq);
        assertThat(listResp.getStatus()).isEqualTo("success");

        // 3. Find By ID
        ApiResponse<MerchantAwardResponse> idResp = queryService.findById(id.intValue());
        assertThat(idResp.getStatus()).isEqualTo("success");

        // 4. Update
        UpdateMerchantAwardRequest updateReq = new UpdateMerchantAwardRequest();
        updateReq.setMerchantCertificationId(id.intValue());
        updateReq.setTitle("ISO 14001");
        updateReq.setDescription("Updated Description");
        updateReq.setIssuedBy("ISO Org");
        updateReq.setIssueDate("2024-02-01");
        updateReq.setExpiryDate("2025-02-01");
        updateReq.setCertificateUrl("https://updated-award.com");

        ApiResponse<MerchantAwardResponse> updateResp = commandService.updateMerchantAward(updateReq);
        assertThat(updateResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 5. Find By Active
        ApiResponsePagination<List<MerchantAwardResponseDeleteAt>> activeResp = queryService.findByActive(findReq);
        assertThat(activeResp.getStatus()).isEqualTo("success");

        // 6. Trash
        ApiResponse<MerchantAwardResponseDeleteAt> trashResp = commandService.trashedMerchantAward(id.intValue());
        assertThat(trashResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 7. Find By Trashed
        ApiResponsePagination<List<MerchantAwardResponseDeleteAt>> trashedResp = queryService.findByTrashed(findReq);
        assertThat(trashedResp.getStatus()).isEqualTo("success");

        // 8. Restore
        ApiResponse<MerchantAwardResponseDeleteAt> restoreResp = commandService.restoreMerchantAward(id.intValue());
        assertThat(restoreResp.getStatus()).isEqualTo("success");

        // Trash again before delete permanent so deleted_at is not null
        ApiResponse<MerchantAwardResponseDeleteAt> trashAgainResp = commandService.trashedMerchantAward(id.intValue());
        assertThat(trashAgainResp.getStatus()).isEqualTo("success");

        // 9. Delete Permanent
        ApiResponse<Boolean> delPermResp = commandService.deleteMerchantAwardPermanent(id.intValue());
        assertThat(delPermResp.getStatus()).isEqualTo("success");

        // 10. Restore All
        ApiResponse<Boolean> restoreAllResp = commandService.restoreAllMerchantAward();
        assertThat(restoreAllResp.getStatus()).isEqualTo("success");

        // 11. Delete All Permanent
        ApiResponse<Boolean> delAllResp = commandService.deleteAllMerchantAwardPermanent();
        assertThat(delAllResp.getStatus()).isEqualTo("success");
    }
}
