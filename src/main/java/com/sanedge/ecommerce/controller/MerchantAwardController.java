package com.sanedge.ecommerce.controller;

import java.util.List;

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
import com.sanedge.ecommerce.domain.requests.merchantawrd.CreateMerchantAwardRequest;
import com.sanedge.ecommerce.domain.requests.merchantawrd.UpdateMerchantAwardRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.merchantaward.MerchantAwardResponse;
import com.sanedge.ecommerce.domain.responses.merchantaward.MerchantAwardResponseDeleteAt;
import com.sanedge.ecommerce.service.merchantaward.MerchantAwardCommandService;
import com.sanedge.ecommerce.service.merchantaward.MerchantAwardQueryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/merchant-award")
public class MerchantAwardController {

    private final MerchantAwardQueryService queryService;
    private final MerchantAwardCommandService commandService;

    @GetMapping
    public ResponseEntity<ApiResponsePagination<List<MerchantAwardResponse>>> findAll(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(queryService.findAll(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MerchantAwardResponse>> findById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(queryService.findById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponsePagination<List<MerchantAwardResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(queryService.findByActive(req));
    }

    @GetMapping("/trashed")
    public ResponseEntity<ApiResponsePagination<List<MerchantAwardResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(queryService.findByTrashed(req));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<MerchantAwardResponse>> create(
            @Valid @RequestBody CreateMerchantAwardRequest req) {
        return ResponseEntity.ok(commandService.createMerchantAward(req));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ApiResponse<MerchantAwardResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMerchantAwardRequest req) {
        req.setMerchantCertificationId(id);
        return ResponseEntity.ok(commandService.updateMerchantAward(req));
    }

    @PostMapping("/trashed/{id}")
    public ResponseEntity<ApiResponse<MerchantAwardResponseDeleteAt>> trashed(
            @PathVariable Integer id) {
        return ResponseEntity.ok(commandService.trashedMerchantAward(id));
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<ApiResponse<MerchantAwardResponseDeleteAt>> restore(
            @PathVariable Integer id) {
        return ResponseEntity.ok(commandService.restoreMerchantAward(id));
    }

    @DeleteMapping("/permanent/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(
            @PathVariable Integer id) {
        return ResponseEntity.ok(commandService.deleteMerchantAwardPermanent(id));
    }

    @PostMapping("/restore/all")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(commandService.restoreAllMerchantAward());
    }

    @PostMapping("/permanent/all")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllPermanent() {
        return ResponseEntity.ok(commandService.deleteAllMerchantAwardPermanent());
    }
}
