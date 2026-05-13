package com.sanedge.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.domain.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.domain.requests.merchantbusiness.CreateMerchantBusinessRequest;
import com.sanedge.ecommerce.domain.requests.merchantbusiness.UpdateMerchantBusinessRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.merchantbusiness.MerchantBusinessResponse;
import com.sanedge.ecommerce.domain.responses.merchantbusiness.MerchantBusinessResponseDeleteAt;
import com.sanedge.ecommerce.enums.Status;
import com.sanedge.ecommerce.models.merchant.Merchant;
import com.sanedge.ecommerce.repository.merchant.MerchantCommandRepository;
import com.sanedge.ecommerce.service.merchantbusiness.MerchantBusinessCommandService;
import com.sanedge.ecommerce.service.merchantbusiness.MerchantBusinessQueryService;

import jakarta.validation.Validator;

public class MerchantBusinessServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MerchantBusinessCommandService commandService;

    @Autowired
    private MerchantBusinessQueryService queryService;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @MockBean
    private Validator validator;

    private Merchant merchant;

    @BeforeEach
    void setup() {
        Merchant m = new Merchant();
        m.setUserId(adminUser.getUserId().intValue());
        m.setName("ServiceBizMerchant");
        m.setDescription("Merchant for business tests");
        m.setAddress("Address");
        m.setContactEmail("service-biz@test.com");
        m.setContactPhone("12345");
        m.setStatus(Status.SUCCESS);
        this.merchant = merchantCommandRepository.save(m);
    }

    @Test
    void testAllMerchantBusinessServiceMethods() {
        // 1. Create
        CreateMerchantBusinessRequest req = new CreateMerchantBusinessRequest();
        req.setMerchantId(merchant.getMerchantId().intValue());
        req.setBusinessType("Enterprise");
        req.setTaxId("99-888-777");
        req.setEstablishedYear(2018);
        req.setNumberOfEmployees(100);
        req.setWebsiteUrl("https://enterprise.com");

        ApiResponse<MerchantBusinessResponse> createResp = commandService.createMerchantBusiness(req);
        assertThat(createResp.getStatus()).isEqualTo("success");
        Long id = createResp.getData().getId();

        entityManager.flush();
        entityManager.clear();

        // 2. Find All
        FindAllMerchantRequest findReq = new FindAllMerchantRequest();
        findReq.setSearch("Enterprise");
        findReq.setPage(1);
        findReq.setPageSize(10);

        ApiResponsePagination<List<MerchantBusinessResponse>> listResp = queryService.findAll(findReq);
        assertThat(listResp.getStatus()).isEqualTo("success");

        // 3. Find By ID
        ApiResponse<MerchantBusinessResponse> idResp = queryService.findById(id.intValue());
        assertThat(idResp.getStatus()).isEqualTo("success");

        // 4. Update
        UpdateMerchantBusinessRequest updateReq = new UpdateMerchantBusinessRequest();
        updateReq.setMerchantBusinessInfoId(id.intValue());
        updateReq.setBusinessType("Startup");
        updateReq.setTaxId("88-777-666");
        updateReq.setEstablishedYear(2021);
        updateReq.setNumberOfEmployees(10);
        updateReq.setWebsiteUrl("https://startup.com");

        ApiResponse<MerchantBusinessResponse> updateResp = commandService.updateMerchantBusiness(updateReq);
        assertThat(updateResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 5. Find By Active
        ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>> activeResp = queryService.findByActive(findReq);
        assertThat(activeResp.getStatus()).isEqualTo("success");

        // 6. Trash
        ApiResponse<MerchantBusinessResponseDeleteAt> trashResp = commandService.trashedMerchantBusiness(id.intValue());
        assertThat(trashResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 7. Find By Trashed
        ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>> trashedResp = queryService.findByTrashed(findReq);
        assertThat(trashedResp.getStatus()).isEqualTo("success");

        // 8. Restore
        ApiResponse<MerchantBusinessResponseDeleteAt> restoreResp = commandService.restoreMerchantBusiness(id.intValue());
        assertThat(restoreResp.getStatus()).isEqualTo("success");

        // Trash again before delete permanent so deleted_at is not null
        ApiResponse<MerchantBusinessResponseDeleteAt> trashAgainResp = commandService.trashedMerchantBusiness(id.intValue());
        assertThat(trashAgainResp.getStatus()).isEqualTo("success");

        // 9. Delete Permanent
        ApiResponse<Boolean> delPermResp = commandService.deleteMerchantBusinessPermanent(id.intValue());
        assertThat(delPermResp.getStatus()).isEqualTo("success");

        // 10. Restore All
        ApiResponse<Boolean> restoreAllResp = commandService.restoreAllMerchantBusiness();
        assertThat(restoreAllResp.getStatus()).isEqualTo("success");

        // 11. Delete All Permanent
        ApiResponse<Boolean> delAllResp = commandService.deleteAllMerchantBusinessPermanent();
        assertThat(delAllResp.getStatus()).isEqualTo("success");
    }
}
