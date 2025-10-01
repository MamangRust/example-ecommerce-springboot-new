package com.sanedge.ecommerce.service.review;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.review.FindAllReview;
import com.sanedge.ecommerce.domain.requests.review.FindAllReviewByMerchant;
import com.sanedge.ecommerce.domain.requests.review.FindAllReviewByProduct;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.reviews.ReviewRelationsDetailResponse;
import com.sanedge.ecommerce.domain.responses.reviews.ReviewResponse;
import com.sanedge.ecommerce.domain.responses.reviews.ReviewResponseDeleteAt;

public interface ReviewQueryService {
    ApiResponsePagination<List<ReviewResponse>> findAll(FindAllReview findAllReview);

    ApiResponsePagination<List<ReviewResponseDeleteAt>> findActive(FindAllReview findAllReview);

    ApiResponsePagination<List<ReviewResponseDeleteAt>> findTrashed(FindAllReview findAllReview);

    ApiResponsePagination<List<ReviewRelationsDetailResponse>> findByMerchant(
            FindAllReviewByMerchant findAllReviewByMerchant);

    ApiResponsePagination<List<ReviewRelationsDetailResponse>> findByProduct(
            FindAllReviewByProduct findAllReviewByProduct);

    ApiResponse<ReviewResponse> findById(Integer reviewId);
}
