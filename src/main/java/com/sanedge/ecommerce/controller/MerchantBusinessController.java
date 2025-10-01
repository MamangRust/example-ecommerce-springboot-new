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
import com.sanedge.ecommerce.domain.requests.merchantbusiness.CreateMerchantBusinessRequest;
import com.sanedge.ecommerce.domain.requests.merchantbusiness.UpdateMerchantBusinessRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.merchantbusiness.MerchantBusinessResponse;
import com.sanedge.ecommerce.domain.responses.merchantbusiness.MerchantBusinessResponseDeleteAt;
import com.sanedge.ecommerce.service.merchantbusiness.MerchantBusinessCommandService;
import com.sanedge.ecommerce.service.merchantbusiness.MerchantBusinessQueryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchant-business")
public class MerchantBusinessController {
    private final MerchantBusinessQueryService queryService;
    private final MerchantBusinessCommandService commandService;

    @GetMapping
    public ResponseEntity<ApiResponsePagination<List<MerchantBusinessResponse>>> findAll(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(queryService.findAll(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MerchantBusinessResponse>> findById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(queryService.findById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(queryService.findByActive(req));
    }

    @GetMapping("/trashed")
    public ResponseEntity<ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(queryService.findByTrashed(req));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<MerchantBusinessResponse>> create(
            @Valid @RequestBody CreateMerchantBusinessRequest req) {
        return ResponseEntity.ok(commandService.createMerchantBusiness(req));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ApiResponse<MerchantBusinessResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMerchantBusinessRequest req) {
        req.setMerchantBusinessInfoId(id);
        return ResponseEntity.ok(commandService.updateMerchantBusiness(req));
    }

    @PostMapping("/trashed/{id}")
    public ResponseEntity<ApiResponse<MerchantBusinessResponseDeleteAt>> trashed(
            @PathVariable Integer id) {
        return ResponseEntity.ok(commandService.trashedMerchantBusiness(id));
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<ApiResponse<MerchantBusinessResponseDeleteAt>> restore(
            @PathVariable Integer id) {
        return ResponseEntity.ok(commandService.restoreMerchantBusiness(id));
    }

    @DeleteMapping("/permanent/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(
            @PathVariable Integer id) {
        return ResponseEntity.ok(commandService.deleteMerchantBusinessPermanent(id));
    }

    @PostMapping("/restore/all")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(commandService.restoreAllMerchantBusiness());
    }

    @PostMapping("/permanent/all")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllPermanent() {
        return ResponseEntity.ok(commandService.deleteAllMerchantBusinessPermanent());
    }
}
