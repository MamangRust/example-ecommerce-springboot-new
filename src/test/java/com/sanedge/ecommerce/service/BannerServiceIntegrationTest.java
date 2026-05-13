package com.sanedge.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.domain.requests.banner.CreateBannerRequest;
import com.sanedge.ecommerce.domain.requests.banner.FindAllBannerRequest;
import com.sanedge.ecommerce.domain.requests.banner.UpdateBannerRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.banner.BannerResponse;
import com.sanedge.ecommerce.domain.responses.banner.BannerResponseDeleteAt;
import com.sanedge.ecommerce.service.banner.BannerCommandService;
import com.sanedge.ecommerce.service.banner.BannerQueryService;

import jakarta.validation.Validator;

public class BannerServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private BannerCommandService commandService;

    @Autowired
    private BannerQueryService queryService;

    @MockBean
    private Validator validator;

    @Test
    void testAllBannerServiceMethods() {
        // 1. Create
        CreateBannerRequest req = new CreateBannerRequest();
        req.setName("UniqueServiceBanner");
        req.setStartDate("2026-05-01");
        req.setEndDate("2026-05-31");
        req.setStartTime("10:00");
        req.setEndTime("22:00");
        req.setIsActive(true);

        ApiResponse<BannerResponse> createResp = commandService.createBanner(req);
        assertThat(createResp.getStatus()).isEqualTo("success");
        Long id = createResp.getData().getId();

        entityManager.flush();
        entityManager.clear();

        // 2. Find All
        FindAllBannerRequest findReq = new FindAllBannerRequest();
        findReq.setSearch("UniqueServiceBanner");
        findReq.setPage(1);
        findReq.setPageSize(10);

        ApiResponsePagination<List<BannerResponse>> listResp = queryService.findAll(findReq);
        assertThat(listResp.getStatus()).isEqualTo("success");

        // 3. Find By ID
        ApiResponse<BannerResponse> idResp = queryService.findById(id.intValue());
        assertThat(idResp.getStatus()).isEqualTo("success");

        // 4. Update
        UpdateBannerRequest updateReq = new UpdateBannerRequest();
        updateReq.setBannerID(id.intValue());
        updateReq.setName("UpdatedBannerName");
        updateReq.setStartDate("2026-06-01");
        updateReq.setEndDate("2026-06-30");
        updateReq.setStartTime("11:00");
        updateReq.setEndTime("23:00");
        updateReq.setIsActive(true);

        ApiResponse<BannerResponse> updateResp = commandService.updateBanner(updateReq);
        assertThat(updateResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 5. Find By Active
        ApiResponsePagination<List<BannerResponseDeleteAt>> activeResp = queryService.findByActive(findReq);
        assertThat(activeResp.getStatus()).isEqualTo("success");

        // 6. Trash
        ApiResponse<BannerResponseDeleteAt> trashResp = commandService.trashedBanner(id.intValue());
        assertThat(trashResp.getStatus()).isEqualTo("success");

        entityManager.flush();
        entityManager.clear();

        // 7. Find By Trashed
        ApiResponsePagination<List<BannerResponseDeleteAt>> trashedResp = queryService.findByTrashed(findReq);
        assertThat(trashedResp.getStatus()).isEqualTo("success");

        // 8. Restore
        ApiResponse<BannerResponseDeleteAt> restoreResp = commandService.restoreBanner(id.intValue());
        assertThat(restoreResp.getStatus()).isEqualTo("success");

        // Trash again before permanent delete
        ApiResponse<BannerResponseDeleteAt> trashAgainResp = commandService.trashedBanner(id.intValue());
        assertThat(trashAgainResp.getStatus()).isEqualTo("success");

        // 9. Delete Permanent
        ApiResponse<Boolean> delPermResp = commandService.deleteBannerPermanent(id.intValue());
        assertThat(delPermResp.getStatus()).isEqualTo("success");

        // 10. Restore All
        ApiResponse<Boolean> restoreAllResp = commandService.restoreAllBanner();
        assertThat(restoreAllResp.getStatus()).isEqualTo("success");

        // 11. Delete All Permanent
        ApiResponse<Boolean> delAllResp = commandService.deleteAllBannerPermanent();
        assertThat(delAllResp.getStatus()).isEqualTo("success");
    }
}
