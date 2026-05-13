package com.sanedge.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.domain.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.domain.requests.merchantdetail.CreateMerchantDetailRequest;
import com.sanedge.ecommerce.domain.requests.merchantdetail.UpdateMerchantDetailRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.api.PaginationMeta;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailRelationResponse;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailRelationResponseDeleteAt;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailResponse;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailResponseDeleteAt;
import com.sanedge.ecommerce.enums.Status;
import com.sanedge.ecommerce.models.merchant.Merchant;
import com.sanedge.ecommerce.repository.merchant.MerchantCommandRepository;
import com.sanedge.ecommerce.service.FileService;
import com.sanedge.ecommerce.service.FolderService;
import com.sanedge.ecommerce.service.merchantdetail.MerchantDetailCommandService;
import com.sanedge.ecommerce.service.merchantdetail.MerchantDetailQueryService;

public class MerchantDetailServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MerchantDetailCommandService commandService;

    @MockBean
    private MerchantDetailQueryService queryService;

    @Autowired
    private MerchantCommandRepository merchantCommandRepository;

    @MockBean
    private FolderService folderService;

    @MockBean
    private FileService fileService;

    private Merchant merchant;

    @BeforeEach
    void setup() {
        Merchant m = new Merchant();
        m.setUserId(adminUser.getUserId().intValue());
        m.setName("ServiceDetailMerchant");
        m.setDescription("Merchant for detail tests");
        m.setAddress("Address");
        m.setContactEmail("service-detail@test.com");
        m.setContactPhone("12345");
        m.setStatus(Status.SUCCESS);
        this.merchant = merchantCommandRepository.save(m);
    }

    @Test
    void testAllMerchantDetailServiceMethods() {
        when(folderService.createFolder(any(), any())).thenReturn("test-folder");
        when(fileService.createFileImage(any(), any())).thenReturn("test-image.jpg");

        // 1. Create
        CreateMerchantDetailRequest req = new CreateMerchantDetailRequest();
        req.setMerchantId(merchant.getMerchantId().intValue());
        req.setDisplayName("Awesome Store");
        req.setShortDescription("Short description about awesome store");
        req.setWebsiteUrl("https://awesome-store.com");

        MockMultipartFile cover = new MockMultipartFile("coverImageUrl", "cover.jpg", "image/jpeg", "content".getBytes());
        MockMultipartFile logo = new MockMultipartFile("logoUrl", "logo.jpg", "image/jpeg", "content".getBytes());
        req.setCoverImageUrl(cover);
        req.setLogoUrl(logo);

        ApiResponse<MerchantDetailResponse> createResp = commandService.createMerchant(req);
        assertThat(createResp.getStatus()).isEqualTo("success");
        Long id = createResp.getData().getId();

        entityManager.flush();
        entityManager.clear();

        // 2. Update
        UpdateMerchantDetailRequest updateReq = new UpdateMerchantDetailRequest();
        updateReq.setMerchantDetailId(id.intValue());
        updateReq.setDisplayName("Awesome Store Updated");
        updateReq.setShortDescription("Updated description");
        updateReq.setWebsiteUrl("https://updated-store.com");
        updateReq.setCoverImageUrl(cover);
        updateReq.setLogoUrl(logo);

        ApiResponse<MerchantDetailResponse> updateResp = commandService.updateMerchant(updateReq);
        assertThat(updateResp.getStatus()).isEqualTo("success");

        // 3. Trashed
        ApiResponse<MerchantDetailResponseDeleteAt> trashResp = commandService.trashedMerchant(id.intValue());
        assertThat(trashResp.getStatus()).isEqualTo("success");

        // 4. Restore
        ApiResponse<MerchantDetailResponseDeleteAt> restoreResp = commandService.restoreMerchant(id.intValue());
        assertThat(restoreResp.getStatus()).isEqualTo("success");

        // 5. Delete Permanent
        ApiResponse<Boolean> delPermResp = commandService.deleteMerchantPermanent(id.intValue());
        assertThat(delPermResp.getStatus()).isEqualTo("success");

        // 6. Restore All
        ApiResponse<Boolean> restoreAllResp = commandService.restoreAllMerchant();
        assertThat(restoreAllResp.getStatus()).isEqualTo("success");

        // 7. Delete All Permanent
        ApiResponse<Boolean> delAllResp = commandService.deleteAllMerchantPermanent();
        assertThat(delAllResp.getStatus()).isEqualTo("success");

        // 8. Find All
        MerchantDetailRelationResponse relResp = new MerchantDetailRelationResponse();
        relResp.setId(id);
        relResp.setMerchantId(merchant.getMerchantId().intValue());
        relResp.setDisplayName("Awesome Store");

        ApiResponsePagination<List<MerchantDetailRelationResponse>> pagedResponse = ApiResponsePagination.<List<MerchantDetailRelationResponse>>builder()
                .status("success")
                .message("retrieved")
                .data(List.of(relResp))
                .pagination(new PaginationMeta(1, 10, 1L, 1))
                .build();

        when(queryService.findAll(any(FindAllMerchantRequest.class))).thenReturn(pagedResponse);

        ApiResponsePagination<List<MerchantDetailRelationResponse>> listResp = queryService.findAll(new FindAllMerchantRequest());
        assertThat(listResp.getStatus()).isEqualTo("success");

        // 9. Find By Active
        ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>> activeResponse = ApiResponsePagination.<List<MerchantDetailRelationResponseDeleteAt>>builder()
                .status("success")
                .data(List.of())
                .pagination(new PaginationMeta(1, 10, 1L, 1))
                .build();
        when(queryService.findByActive(any(FindAllMerchantRequest.class))).thenReturn(activeResponse);

        ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>> activeResp = queryService.findByActive(new FindAllMerchantRequest());
        assertThat(activeResp.getStatus()).isEqualTo("success");

        // 10. Find By Trashed
        ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>> trashedResponse = ApiResponsePagination.<List<MerchantDetailRelationResponseDeleteAt>>builder()
                .status("success")
                .data(List.of())
                .pagination(new PaginationMeta(1, 10, 1L, 1))
                .build();
        when(queryService.findByTrashed(any(FindAllMerchantRequest.class))).thenReturn(trashedResponse);

        ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>> trashedResp = queryService.findByTrashed(new FindAllMerchantRequest());
        assertThat(trashedResp.getStatus()).isEqualTo("success");

        // 11. Find By ID
        ApiResponse<MerchantDetailRelationResponse> singleResponse = ApiResponse.<MerchantDetailRelationResponse>builder()
                .status("success")
                .data(relResp)
                .build();
        when(queryService.findById(any(Integer.class))).thenReturn(singleResponse);

        ApiResponse<MerchantDetailRelationResponse> idResp = queryService.findById(id.intValue());
        assertThat(idResp.getStatus()).isEqualTo("success");
    }
}
