package com.sanedge.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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
import com.sanedge.ecommerce.domain.requests.merchantpolicy.CreateMerchantPolicyRequest;
import com.sanedge.ecommerce.domain.requests.merchantpolicy.UpdateMerchantPolicyRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.merchantpolicy.MerchantPoliciesResponse;
import com.sanedge.ecommerce.domain.responses.merchantpolicy.MerchantPoliciesResponseDeleteAt;
import com.sanedge.ecommerce.service.merchantpolicy.MerchantPolicyCommandService;
import com.sanedge.ecommerce.service.merchantpolicy.MerchantPolicyQueryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/merchant-policy")
public class MerchantPolicyController {

    private final MerchantPolicyQueryService queryService;
    private final MerchantPolicyCommandService commandService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<MerchantPoliciesResponse>>> findAll(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(queryService.findAll(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<MerchantPoliciesResponse>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(queryService.findById(id));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<MerchantPoliciesResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(queryService.findByActive(req));
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<MerchantPoliciesResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllMerchantRequest req) {
        return ResponseEntity.ok(queryService.findByTrashed(req));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantPoliciesResponse>> create(
            @Valid @RequestBody CreateMerchantPolicyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commandService.create(request));
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantPoliciesResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateMerchantPolicyRequest request) {
        request.setMerchantPolicyId(id);
        return ResponseEntity.ok(commandService.update(request));
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantPoliciesResponseDeleteAt>> trashed(@PathVariable Integer id) {
        return ResponseEntity.ok(commandService.trash(id));
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<MerchantPoliciesResponseDeleteAt>> restore(@PathVariable Integer id) {
        return ResponseEntity.ok(commandService.restore(id));
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Integer id) {
        return ResponseEntity.ok(commandService.delete(id));
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(commandService.restoreAll());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllPermanent() {
        return ResponseEntity.ok(commandService.deleteAll());
    }
}
