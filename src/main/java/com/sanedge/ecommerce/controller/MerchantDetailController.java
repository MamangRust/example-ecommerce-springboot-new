package com.sanedge.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchant-detail")
public class MerchantDetailController {

    private final MerchantDetailQueryService queryService;
    private final MerchantDetailCommandService commandService;

    @GetMapping
    public ResponseEntity<ApiResponsePagination<List<MerchantDetailRelationResponse>>> findAll(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(queryService.findAll(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MerchantDetailRelationResponse>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(queryService.findById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(queryService.findByActive(req));
    }

    @GetMapping("/trashed")
    public ResponseEntity<ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(queryService.findByTrashed(req));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<MerchantDetailResponse>> createMerchant(
            @Valid @RequestBody CreateMerchantDetailRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commandService.createMerchant(req));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ApiResponse<MerchantDetailResponse>> updateMerchant(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMerchantDetailRequest req) {
        req.setMerchantDetailId(id);
        return ResponseEntity.ok(commandService.updateMerchant(req));
    }

    @PostMapping("/trashed/{id}")
    public ResponseEntity<ApiResponse<MerchantDetailResponseDeleteAt>> trashedMerchant(@PathVariable Integer id) {
        return ResponseEntity.ok(commandService.trashedMerchant(id));
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<ApiResponse<MerchantDetailResponseDeleteAt>> restoreMerchant(@PathVariable Integer id) {
        return ResponseEntity.ok(commandService.restoreMerchant(id));
    }

    @DeleteMapping("/permanent/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteMerchantPermanent(@PathVariable Integer id) {
        return ResponseEntity.ok(commandService.deleteMerchantPermanent(id));
    }

    @PostMapping("/restore/all")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllMerchant() {
        return ResponseEntity.ok(commandService.restoreAllMerchant());
    }

    @PostMapping("/permanent/all")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllMerchantPermanent() {
        return ResponseEntity.ok(commandService.deleteAllMerchantPermanent());
    }
}
