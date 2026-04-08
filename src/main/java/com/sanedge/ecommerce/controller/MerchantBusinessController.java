package com.sanedge.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<MerchantBusinessResponse>>> findAll(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(queryService.findAll(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<MerchantBusinessResponse>> findById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(queryService.findById(id));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(queryService.findByActive(req));
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(queryService.findByTrashed(req));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantBusinessResponse>> create(
            @Valid @RequestBody CreateMerchantBusinessRequest req) {
        return ResponseEntity.ok(commandService.createMerchantBusiness(req));
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantBusinessResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMerchantBusinessRequest req) {
        req.setMerchantBusinessInfoId(id);
        return ResponseEntity.ok(commandService.updateMerchantBusiness(req));
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantBusinessResponseDeleteAt>> trashed(
            @PathVariable Integer id) {
        return ResponseEntity.ok(commandService.trashedMerchantBusiness(id));
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantBusinessResponseDeleteAt>> restore(
            @PathVariable Integer id) {
        return ResponseEntity.ok(commandService.restoreMerchantBusiness(id));
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(
            @PathVariable Integer id) {
        return ResponseEntity.ok(commandService.deleteMerchantBusinessPermanent(id));
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(commandService.restoreAllMerchantBusiness());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllPermanent() {
        return ResponseEntity.ok(commandService.deleteAllMerchantBusinessPermanent());
    }
}
