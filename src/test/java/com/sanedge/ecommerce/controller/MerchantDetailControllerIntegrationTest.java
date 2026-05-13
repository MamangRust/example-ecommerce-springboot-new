package com.sanedge.ecommerce.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sanedge.ecommerce.domain.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.domain.requests.merchantdetail.CreateMerchantDetailRequest;
import com.sanedge.ecommerce.domain.requests.merchantdetail.UpdateMerchantDetailRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailRelationResponse;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailRelationResponseDeleteAt;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailResponse;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailResponseDeleteAt;
import com.sanedge.ecommerce.service.merchantdetail.MerchantDetailCommandService;
import com.sanedge.ecommerce.service.merchantdetail.MerchantDetailQueryService;

public class MerchantDetailControllerIntegrationTest {

    private MerchantDetailController controller;
    private MerchantDetailCommandService commandService;
    private MerchantDetailQueryService queryService;

    @BeforeEach
    void setup() {
        commandService = mock(MerchantDetailCommandService.class);
        queryService = mock(MerchantDetailQueryService.class);
        controller = new MerchantDetailController(queryService, commandService);
    }

    @Test
    void shouldPerformAllMerchantDetailEndpoints() {
        FindAllMerchantRequest req = new FindAllMerchantRequest();

        // 1. Find All
        when(queryService.findAll(any())).thenReturn(ApiResponsePagination.<List<MerchantDetailRelationResponse>>builder()
                .status("success")
                .data(new ArrayList<>())
                .build());

        ResponseEntity<ApiResponsePagination<List<MerchantDetailRelationResponse>>> r1 = controller.findAll(req);
        assertThat(r1.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 2. Find By ID
        when(queryService.findById(anyInt())).thenReturn(ApiResponse.<MerchantDetailRelationResponse>builder()
                .status("success")
                .data(new MerchantDetailRelationResponse())
                .build());

        ResponseEntity<ApiResponse<MerchantDetailRelationResponse>> r2 = controller.findById(1);
        assertThat(r2.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 3. Find Active
        when(queryService.findByActive(any())).thenReturn(ApiResponsePagination.<List<MerchantDetailRelationResponseDeleteAt>>builder()
                .status("success")
                .data(new ArrayList<>())
                .build());

        ResponseEntity<ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>>> r3 = controller.findByActive(req);
        assertThat(r3.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 4. Find Trashed
        when(queryService.findByTrashed(any())).thenReturn(ApiResponsePagination.<List<MerchantDetailRelationResponseDeleteAt>>builder()
                .status("success")
                .data(new ArrayList<>())
                .build());

        ResponseEntity<ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>>> r4 = controller.findByTrashed(req);
        assertThat(r4.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 5. Create
        when(commandService.createMerchant(any())).thenReturn(ApiResponse.<MerchantDetailResponse>builder()
                .status("success")
                .message("Created")
                .data(new MerchantDetailResponse())
                .build());

        CreateMerchantDetailRequest createReq = new CreateMerchantDetailRequest();
        createReq.setMerchantId(1);
        createReq.setDisplayName("Test Store");

        ResponseEntity<ApiResponse<MerchantDetailResponse>> r5 = controller.createMerchant(createReq);
        assertThat(r5.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // 6. Update
        when(commandService.updateMerchant(any())).thenReturn(ApiResponse.<MerchantDetailResponse>builder()
                .status("success")
                .message("Updated")
                .data(new MerchantDetailResponse())
                .build());

        UpdateMerchantDetailRequest updateReq = new UpdateMerchantDetailRequest();
        updateReq.setMerchantDetailId(1);
        updateReq.setDisplayName("Updated Store");

        ResponseEntity<ApiResponse<MerchantDetailResponse>> r6 = controller.updateMerchant(1, updateReq);
        assertThat(r6.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 7. Trash
        when(commandService.trashedMerchant(anyInt())).thenReturn(ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                .status("success")
                .message("Trashed")
                .data(new MerchantDetailResponseDeleteAt())
                .build());

        ResponseEntity<ApiResponse<MerchantDetailResponseDeleteAt>> r7 = controller.trashedMerchant(1);
        assertThat(r7.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 8. Restore
        when(commandService.restoreMerchant(anyInt())).thenReturn(ApiResponse.<MerchantDetailResponseDeleteAt>builder()
                .status("success")
                .message("Restored")
                .data(new MerchantDetailResponseDeleteAt())
                .build());

        ResponseEntity<ApiResponse<MerchantDetailResponseDeleteAt>> r8 = controller.restoreMerchant(1);
        assertThat(r8.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 9. Permanent Delete
        when(commandService.deleteMerchantPermanent(anyInt())).thenReturn(ApiResponse.<Boolean>builder()
                .status("success")
                .data(true)
                .build());

        ResponseEntity<ApiResponse<Boolean>> r9 = controller.deleteMerchantPermanent(1);
        assertThat(r9.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 10. Restore All
        when(commandService.restoreAllMerchant()).thenReturn(ApiResponse.<Boolean>builder()
                .status("success")
                .data(true)
                .build());

        ResponseEntity<ApiResponse<Boolean>> r10 = controller.restoreAllMerchant();
        assertThat(r10.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 11. Delete All (permanent)
        when(commandService.deleteAllMerchantPermanent()).thenReturn(ApiResponse.<Boolean>builder()
                .status("success")
                .data(true)
                .build());

        ResponseEntity<ApiResponse<Boolean>> r11 = controller.deleteAllMerchantPermanent();
        assertThat(r11.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
