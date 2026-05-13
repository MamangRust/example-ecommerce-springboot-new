package com.sanedge.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.domain.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.domain.requests.merchantpolicy.CreateMerchantPolicyRequest;
import com.sanedge.ecommerce.domain.requests.merchantpolicy.UpdateMerchantPolicyRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.merchantpolicy.MerchantPoliciesResponse;
import com.sanedge.ecommerce.domain.responses.merchantpolicy.MerchantPoliciesResponseDeleteAt;
import com.sanedge.ecommerce.enums.Status;
import com.sanedge.ecommerce.models.merchant.Merchant;
import com.sanedge.ecommerce.repository.merchant.MerchantCommandRepository;
import com.sanedge.ecommerce.service.merchantpolicy.MerchantPolicyCommandService;
import com.sanedge.ecommerce.service.merchantpolicy.MerchantPolicyQueryService;

public class MerchantPolicyServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MerchantPolicyCommandService commandService;

    @Autowired
    private MerchantPolicyQueryService queryService;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    private Merchant merchant;

    @BeforeEach
    void setup() {
        Merchant m = new Merchant();
        m.setUserId(adminUser.getUserId().intValue());
        m.setName("ServicePolicyMerchant");
        m.setDescription("Merchant for policy tests");
        m.setAddress("Address");
        m.setContactEmail("service-policy@test.com");
        m.setContactPhone("12345");
        m.setStatus(Status.SUCCESS);
        this.merchant = merchantCommandRepository.save(m);
    }

    @Test
    void testAllMerchantPolicyServiceMethods() {
        // 1. Create
        CreateMerchantPolicyRequest req = new CreateMerchantPolicyRequest();
        req.setMerchantId(merchant.getMerchantId().intValue());
        req.setPolicyType("Shipping");
        req.setTitle("Standard Shipping Policy");
        req.setDescription("Standard ground shipping in 3-5 days");

        ApiResponse<MerchantPoliciesResponse> createResp = commandService.create(req);
        assertThat(createResp.getStatus()).isEqualTo("success");
        Long id = createResp.getData().getId();

        entityManager.flush();
        entityManager.clear();

        // 2. Find All
        FindAllMerchantRequest findReq = new FindAllMerchantRequest();
        findReq.setSearch("Shipping");
        findReq.setPage(1);
        findReq.setPageSize(10);

        ApiResponsePagination<List<MerchantPoliciesResponse>> listResp = queryService.findAll(findReq);
        assertThat(listResp.getStatus()).isEqualTo("success");

        // 3. Find By ID
        ApiResponse<MerchantPoliciesResponse> idResp = queryService.findById(id.intValue());
        assertThat(idResp.getStatus()).isEqualTo("success");

        // 4. Update
        UpdateMerchantPolicyRequest updateReq = new UpdateMerchantPolicyRequest();
        updateReq.setMerchantPolicyId(id.intValue());
        updateReq.setPolicyType("Return");
        updateReq.setTitle("30-Day Return Policy");
        updateReq.setDescription("Return items within 30 days");

        ApiResponse<MerchantPoliciesResponse> updateResp = commandService.update(updateReq);
        assertThat(updateResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 5. Find By Active
        ApiResponsePagination<List<MerchantPoliciesResponseDeleteAt>> activeResp = queryService.findByActive(findReq);
        assertThat(activeResp.getStatus()).isEqualTo("success");

        // 6. Trash
        ApiResponse<MerchantPoliciesResponseDeleteAt> trashResp = commandService.trash(id.intValue());
        assertThat(trashResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 7. Find By Trashed
        ApiResponsePagination<List<MerchantPoliciesResponseDeleteAt>> trashedResp = queryService.findByTrashed(findReq);
        assertThat(trashedResp.getStatus()).isEqualTo("success");

        // 8. Restore
        ApiResponse<MerchantPoliciesResponseDeleteAt> restoreResp = commandService.restore(id.intValue());
        assertThat(restoreResp.getStatus()).isEqualTo("success");

        // Trash again before delete permanent so deleted_at is not null
        ApiResponse<MerchantPoliciesResponseDeleteAt> trashAgainResp = commandService.trash(id.intValue());
        assertThat(trashAgainResp.getStatus()).isEqualTo("success");

        // 9. Delete Permanent
        ApiResponse<Boolean> delPermResp = commandService.delete(id.intValue());
        assertThat(delPermResp.getStatus()).isEqualTo("success");

        // 10. Restore All
        ApiResponse<Boolean> restoreAllResp = commandService.restoreAll();
        assertThat(restoreAllResp.getStatus()).isEqualTo("success");

        // 11. Delete All Permanent
        ApiResponse<Boolean> delAllResp = commandService.deleteAll();
        assertThat(delAllResp.getStatus()).isEqualTo("success");
    }
}
