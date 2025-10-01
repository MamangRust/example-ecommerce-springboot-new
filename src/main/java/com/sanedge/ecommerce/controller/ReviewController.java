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

import com.sanedge.ecommerce.domain.requests.review.CreateReviewRequest;
import com.sanedge.ecommerce.domain.requests.review.FindAllReview;
import com.sanedge.ecommerce.domain.requests.review.FindAllReviewByMerchant;
import com.sanedge.ecommerce.domain.requests.review.FindAllReviewByProduct;
import com.sanedge.ecommerce.domain.requests.review.UpdateReviewRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.reviews.ReviewRelationsDetailResponse;
import com.sanedge.ecommerce.domain.responses.reviews.ReviewResponse;
import com.sanedge.ecommerce.domain.responses.reviews.ReviewResponseDeleteAt;
import com.sanedge.ecommerce.service.review.ReviewCommandService;
import com.sanedge.ecommerce.service.review.ReviewQueryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewQueryService reviewQueryService;
    private final ReviewCommandService reviewCommandService;

    @GetMapping("")
    public ResponseEntity<ApiResponsePagination<List<ReviewResponse>>> findAll(
            @ModelAttribute FindAllReview req) {

        return ResponseEntity.ok(reviewQueryService.findAll(req));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponsePagination<List<ReviewResponseDeleteAt>>> findActive(
            @ModelAttribute FindAllReview req) {
        return ResponseEntity.ok(reviewQueryService.findActive(req));
    }

    @GetMapping("/trashed")
    public ResponseEntity<ApiResponsePagination<List<ReviewResponseDeleteAt>>> findTrashed(
            @ModelAttribute FindAllReview req) {
        return ResponseEntity.ok(reviewQueryService.findTrashed(req));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponsePagination<List<ReviewRelationsDetailResponse>>> findByProduct(
            @PathVariable Integer productId,
            @ModelAttribute FindAllReviewByProduct req) {
        req.setProductId(productId);

        return ResponseEntity.ok(reviewQueryService.findByProduct(req));
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<ApiResponsePagination<List<ReviewRelationsDetailResponse>>> findByMerchant(
            @PathVariable Integer merchantId,
            @ModelAttribute FindAllReviewByMerchant req) {
        req.setMerchantId(merchantId);

        return ResponseEntity.ok(reviewQueryService
                .findByMerchant(req));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> findById(@PathVariable Integer reviewId) {
        return ResponseEntity.ok(reviewQueryService.findById(reviewId));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ReviewResponse>> create(@Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewCommandService.create(request));
    }

    @PostMapping("/update/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> update(
            @PathVariable Integer reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {
        request.setReviewId(reviewId);
        return ResponseEntity.ok(reviewCommandService.update(request));
    }

    @PostMapping("/trashed/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDeleteAt>> trash(@PathVariable Integer reviewId) {
        return ResponseEntity.ok(reviewCommandService.trash(reviewId));
    }

    @PostMapping("/restore/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDeleteAt>> restore(@PathVariable Integer reviewId) {
        return ResponseEntity.ok(reviewCommandService.restore(reviewId));
    }

    @DeleteMapping("/permanent/{reviewId}")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Integer reviewId) {
        return ResponseEntity.ok(reviewCommandService.delete(reviewId));
    }

    @PostMapping("/restore/all")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(reviewCommandService.restoreAll());
    }

    @PostMapping("/permanent/all")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllPermanent() {
        return ResponseEntity.ok(reviewCommandService.deleteAll());
    }
}
