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

import com.sanedge.ecommerce.domain.requests.merchant.CreateMerchantRequest;
import com.sanedge.ecommerce.domain.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.domain.requests.merchant.UpdateMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.merchant.MerchantResponse;
import com.sanedge.ecommerce.domain.responses.merchant.MerchantResponseDeleteAt;
import com.sanedge.ecommerce.service.merchant.MerchantCommandService;
import com.sanedge.ecommerce.service.merchant.MerchantQueryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchant")
public class MerchantController {
    private final MerchantQueryService merchantQueryService;
    private final MerchantCommandService merchantCommandService;

    @GetMapping
    public ResponseEntity<ApiResponsePagination<List<MerchantResponse>>> findAll(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(merchantQueryService.findAll(req));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponsePagination<List<MerchantResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(merchantQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    public ResponseEntity<ApiResponsePagination<List<MerchantResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllMerchantRequest req) {

        return ResponseEntity.ok(merchantQueryService.findByTrashed(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MerchantResponse>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(merchantQueryService.findById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<MerchantResponse>> createMerchant(
            @Valid @RequestBody CreateMerchantRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(merchantCommandService.createMerchant(req));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ApiResponse<MerchantResponse>> updateMerchant(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMerchantRequest req) {
        req.setMerchantId(id);

        return ResponseEntity.ok(merchantCommandService.updateMerchant(req));
    }

    @PostMapping("/trashed/{id}")
    public ResponseEntity<ApiResponse<MerchantResponseDeleteAt>> trashedMerchant(@PathVariable Integer id) {
        return ResponseEntity.ok(merchantCommandService.trashedMerchant(id));
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<ApiResponse<MerchantResponseDeleteAt>> restoreMerchant(@PathVariable Integer id) {
        return ResponseEntity.ok(merchantCommandService.restoreMerchant(id));
    }

    @DeleteMapping("/permanent/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteMerchantPermanent(@PathVariable Integer id) {
        return ResponseEntity.ok(merchantCommandService.deleteMerchantPermanent(id));
    }

    @PostMapping("/restore/all")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllMerchant() {
        return ResponseEntity.ok(merchantCommandService.restoreAllMerchant());
    }

    @PostMapping("/permanent/all")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllMerchantPermanent() {
        return ResponseEntity.ok(merchantCommandService.deleteAllMerchantPermanent());
    }
}