package com.sanedge.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.ecommerce.domain.requests.merchantsociallink.CreateMerchantSocialRequest;
import com.sanedge.ecommerce.domain.requests.merchantsociallink.UpdateMerchantSocialRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.merchantsociallink.MerchantSocialMediaLinkResponse;
import com.sanedge.ecommerce.domain.responses.merchantsociallink.MerchantSocialMediaLinkResponseDeleteAt;
import com.sanedge.ecommerce.service.MerchantSocialLinkService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/merchant-social-link")
public class MerchantSocialLinkController {

    private final MerchantSocialLinkService service;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<MerchantSocialMediaLinkResponse>> create(
            @RequestBody CreateMerchantSocialRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ApiResponse<MerchantSocialMediaLinkResponse>> update(
            @PathVariable Integer id,
            @RequestBody UpdateMerchantSocialRequest request) {
        request.setId(id);
        return ResponseEntity.ok(service.update(request));
    }

    @PostMapping("/trash/{id}")
    public ResponseEntity<ApiResponse<MerchantSocialMediaLinkResponseDeleteAt>> trash(
            @PathVariable Integer id) {
        return ResponseEntity.ok(service.trash(id));
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<ApiResponse<MerchantSocialMediaLinkResponseDeleteAt>> restore(
            @PathVariable Integer id) {
        return ResponseEntity.ok(service.restore(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Boolean>> delete(
            @PathVariable Integer id) {
        return ResponseEntity.ok(service.delete(id));
    }

    @PostMapping("/restore/all")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(service.restoreAll());
    }

    @PostMapping("/delete/all")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        return ResponseEntity.ok(service.deleteAll());
    }
}
