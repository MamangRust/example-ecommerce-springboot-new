package com.sanedge.ecommerce.service.review;

import com.sanedge.ecommerce.domain.requests.review.CreateReviewRequest;
import com.sanedge.ecommerce.domain.requests.review.UpdateReviewRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.reviews.ReviewResponse;
import com.sanedge.ecommerce.domain.responses.reviews.ReviewResponseDeleteAt;

public interface ReviewCommandService {
    ApiResponse<ReviewResponse> create(CreateReviewRequest request);

    ApiResponse<ReviewResponse> update(UpdateReviewRequest request);

    ApiResponse<ReviewResponseDeleteAt> trash(Integer id);

    ApiResponse<ReviewResponseDeleteAt> restore(Integer id);

    ApiResponse<Boolean> delete(Integer id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
