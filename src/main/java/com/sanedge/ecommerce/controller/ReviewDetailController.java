package com.sanedge.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.ecommerce.domain.requests.reviewdetail.CreateReviewDetailRequest;
import com.sanedge.ecommerce.domain.requests.reviewdetail.UpdateReviewDetailRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.reviewdetail.ReviewDetailResponse;
import com.sanedge.ecommerce.domain.responses.reviewdetail.ReviewDetailResponseDeleteAt;
import com.sanedge.ecommerce.service.reviewdetail.ReviewDetailService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review-details")
public class ReviewDetailController {

    private final ReviewDetailService reviewDetailService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReviewDetailResponse>>> create(
            @Valid @ModelAttribute List<CreateReviewDetailRequest> request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDetailService.create(request));
    }

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReviewDetailResponse>>> update(
            @PathVariable Integer reviewDetailId,
            @Valid @ModelAttribute List<UpdateReviewDetailRequest> request) {
        return ResponseEntity.ok(reviewDetailService.update(request));
    }

    @PostMapping("/trashed/{reviewDetailId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewDetailResponseDeleteAt>> trash(
            @PathVariable Integer reviewDetailId) {
        return ResponseEntity.ok(reviewDetailService.trash(reviewDetailId));
    }

    @PatchMapping("/restore/{reviewDetailId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewDetailResponseDeleteAt>> restore(
            @PathVariable Integer reviewDetailId) {
        return ResponseEntity.ok(reviewDetailService.restore(reviewDetailId));
    }

    @DeleteMapping("/permanent/{reviewDetailId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> delete(@PathVariable Integer reviewDetailId) {
        return ResponseEntity.ok(reviewDetailService.delete(reviewDetailId));
    }

    @PatchMapping("/restore-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(reviewDetailService.restoreAll());
    }

    @DeleteMapping("/delete-all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAll() {
        return ResponseEntity.ok(reviewDetailService.deleteAll());
    }
}
