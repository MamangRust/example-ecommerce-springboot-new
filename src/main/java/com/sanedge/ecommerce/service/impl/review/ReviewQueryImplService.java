package com.sanedge.ecommerce.service.impl.review;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.requests.review.FindAllReview;
import com.sanedge.ecommerce.domain.requests.review.FindAllReviewByMerchant;
import com.sanedge.ecommerce.domain.requests.review.FindAllReviewByProduct;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.api.PaginationMeta;
import com.sanedge.ecommerce.domain.responses.reviews.ReviewRelationsDetailResponse;
import com.sanedge.ecommerce.domain.responses.reviews.ReviewResponse;
import com.sanedge.ecommerce.domain.responses.reviews.ReviewResponseDeleteAt;
import com.sanedge.ecommerce.models.review.Review;
import com.sanedge.ecommerce.models.review.ReviewRelationsDetail;
import com.sanedge.ecommerce.repository.review.ReviewQueryRepository;
import com.sanedge.ecommerce.service.review.ReviewQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewQueryImplService implements ReviewQueryService {

        private final ReviewQueryRepository reviewQueryRepository;

        @Override
        public ApiResponsePagination<List<ReviewResponse>> findAll(FindAllReview req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching all reviews | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Review> reviewPage = reviewQueryRepository.findReviews(keyword, pageable);

                        List<ReviewResponse> responses = reviewPage.getContent()
                                        .stream()
                                        .map(ReviewResponse::from)
                                        .toList();

                        log.info("✅ Found {} reviews", responses.size());

                        return ApiResponsePagination.<List<ReviewResponse>>builder()
                                        .status("success")
                                        .message("Reviews retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(reviewPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch reviews", e);
                        return ApiResponsePagination.<List<ReviewResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch reviews")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<ReviewResponseDeleteAt>> findActive(FindAllReview req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching active reviews | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Review> reviewPage = reviewQueryRepository.findActiveReviews(keyword, pageable);

                        List<ReviewResponseDeleteAt> responses = reviewPage.getContent()
                                        .stream()
                                        .map(ReviewResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} active reviews", responses.size());

                        return ApiResponsePagination.<List<ReviewResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Active reviews retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(reviewPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch active reviews", e);
                        return ApiResponsePagination.<List<ReviewResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch active reviews")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<ReviewResponseDeleteAt>> findTrashed(FindAllReview req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🔍 Searching trashed reviews | Page: {}, Size: {}, Search: {}", page + 1, pageSize,
                                keyword.isEmpty() ? "None" : keyword);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<Review> reviewPage = reviewQueryRepository.findTrashedReviews(keyword, pageable);

                        List<ReviewResponseDeleteAt> responses = reviewPage.getContent()
                                        .stream()
                                        .map(ReviewResponseDeleteAt::from)
                                        .toList();

                        log.info("✅ Found {} trashed reviews", responses.size());

                        return ApiResponsePagination.<List<ReviewResponseDeleteAt>>builder()
                                        .status("success")
                                        .message("Trashed reviews retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(reviewPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch trashed reviews", e);
                        return ApiResponsePagination.<List<ReviewResponseDeleteAt>>builder()
                                        .status("error")
                                        .message("Failed to fetch trashed reviews")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<ReviewRelationsDetailResponse>> findByMerchant(FindAllReviewByMerchant req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("🏪 Searching reviews by merchantId={} | rating={}, Page={}, Size={}",
                                req.getMerchantId(), req.getRating(), page + 1, pageSize);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<ReviewRelationsDetail> reviewPage = reviewQueryRepository.findByMerchantId(
                                        req.getMerchantId(), req.getRating(), keyword, pageable);

                        List<ReviewRelationsDetailResponse> responses = reviewPage.getContent()
                                        .stream()
                                        .map(ReviewRelationsDetailResponse::from)
                                        .toList();

                        log.info("✅ Found {} reviews for merchantId={}", responses.size(), req.getMerchantId());

                        return ApiResponsePagination.<List<ReviewRelationsDetailResponse>>builder()
                                        .status("success")
                                        .message("Merchant reviews retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(reviewPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch reviews by merchantId={}", req.getMerchantId(), e);
                        return ApiResponsePagination.<List<ReviewRelationsDetailResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch reviews by merchant")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponsePagination<List<ReviewRelationsDetailResponse>> findByProduct(FindAllReviewByProduct req) {
                int page = req.getPage() > 0 ? req.getPage() - 1 : 0;
                int pageSize = req.getPageSize() > 0 ? req.getPageSize() : 10;
                String keyword = (req.getSearch() != null && !req.getSearch().isEmpty()) ? req.getSearch() : "";

                log.info("📦 Searching reviews by productId={} | rating={}, Page={}, Size={}",
                                req.getProductId(), req.getRating(), page + 1, pageSize);

                try {
                        Pageable pageable = PageRequest.of(page, pageSize);
                        Page<ReviewRelationsDetail> reviewPage = reviewQueryRepository.findByProductId(
                                        req.getProductId(), req.getRating(), keyword, pageable);

                        List<ReviewRelationsDetailResponse> responses = reviewPage.getContent()
                                        .stream()
                                        .map(ReviewRelationsDetailResponse::from)
                                        .toList();

                        log.info("✅ Found {} reviews for productId={}", responses.size(), req.getProductId());

                        return ApiResponsePagination.<List<ReviewRelationsDetailResponse>>builder()
                                        .status("success")
                                        .message("Product reviews retrieved successfully")
                                        .data(responses)
                                        .pagination(PaginationMeta.fromSpringPage(reviewPage))
                                        .build();
                } catch (Exception e) {
                        log.error("💥 Failed to fetch reviews by productId={}", req.getProductId(), e);
                        return ApiResponsePagination.<List<ReviewRelationsDetailResponse>>builder()
                                        .status("error")
                                        .message("Failed to fetch reviews by product")
                                        .data(Collections.emptyList())
                                        .pagination(null)
                                        .build();
                }
        }

        @Override
        public ApiResponse<ReviewResponse> findById(Integer reviewId) {
                log.info("🔍 Finding review by id={}", reviewId);
                try {
                        return reviewQueryRepository.findById(reviewId.longValue())
                                        .map(review -> ApiResponse.<ReviewResponse>builder()
                                                        .status("success")
                                                        .message("Review retrieved successfully")
                                                        .data(ReviewResponse.from(review))
                                                        .build())
                                        .orElseGet(() -> {
                                                log.warn("❌ Review not found with id={}", reviewId);
                                                return ApiResponse.<ReviewResponse>builder()
                                                                .status("error")
                                                                .message("Review not found")
                                                                .data(null)
                                                                .build();
                                        });
                } catch (Exception e) {
                        log.error("💥 Failed to fetch review by id={}", reviewId, e);
                        return ApiResponse.<ReviewResponse>builder()
                                        .status("error")
                                        .message("Failed to fetch review")
                                        .data(null)
                                        .build();
                }
        }
}
