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

import com.sanedge.ecommerce.domain.requests.banner.CreateBannerRequest;
import com.sanedge.ecommerce.domain.requests.banner.FindAllBannerRequest;
import com.sanedge.ecommerce.domain.requests.banner.UpdateBannerRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.banner.BannerResponse;
import com.sanedge.ecommerce.domain.responses.banner.BannerResponseDeleteAt;
import com.sanedge.ecommerce.service.banner.BannerCommandService;
import com.sanedge.ecommerce.service.banner.BannerQueryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/banner")
public class BannerController {
    private final BannerQueryService bannerQueryService;
    private final BannerCommandService bannerCommandService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<BannerResponse>>> findAll(
            @ModelAttribute FindAllBannerRequest req) {
        return ResponseEntity.ok(bannerQueryService.findAll(req));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<BannerResponseDeleteAt>>> findByActive(
            @ModelAttribute FindAllBannerRequest req) {

        return ResponseEntity.ok(bannerQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<BannerResponseDeleteAt>>> findByTrashed(
            @ModelAttribute FindAllBannerRequest req) {

        return ResponseEntity.ok(bannerQueryService.findByTrashed(req));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<BannerResponse>> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(bannerQueryService.findById(id));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<BannerResponse>> createBanner(
            @Valid @RequestBody CreateBannerRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bannerCommandService.createBanner(req));
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<BannerResponse>> updateBanner(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateBannerRequest req) {
        req.setBannerID(id);

        return ResponseEntity.ok(bannerCommandService.updateBanner(req));
    }

    @PostMapping("/trashed/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<BannerResponseDeleteAt>> trashedBanner(@PathVariable Integer id) {
        return ResponseEntity.ok(bannerCommandService.trashedBanner(id));
    }

    @PostMapping("/restore/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<BannerResponseDeleteAt>> restoreBanner(@PathVariable Integer id) {
        return ResponseEntity.ok(bannerCommandService.restoreBanner(id));
    }

    @DeleteMapping("/permanent/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteBannerPermanent(@PathVariable Integer id) {
        return ResponseEntity.ok(bannerCommandService.deleteBannerPermanent(id));
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAllBanner() {
        return ResponseEntity.ok(bannerCommandService.restoreAllBanner());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllBannerPermanent() {
        return ResponseEntity.ok(bannerCommandService.deleteAllBannerPermanent());
    }
}