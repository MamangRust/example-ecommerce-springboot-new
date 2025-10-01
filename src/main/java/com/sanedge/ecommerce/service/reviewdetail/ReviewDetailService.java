package com.sanedge.ecommerce.service.reviewdetail;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.reviewdetail.CreateReviewDetailRequest;
import com.sanedge.ecommerce.domain.requests.reviewdetail.UpdateReviewDetailRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.reviewdetail.ReviewDetailResponse;
import com.sanedge.ecommerce.domain.responses.reviewdetail.ReviewDetailResponseDeleteAt;

public interface ReviewDetailService {
    ApiResponse<List<ReviewDetailResponse>> create(List<CreateReviewDetailRequest> request);

    ApiResponse<List<ReviewDetailResponse>> update(List<UpdateReviewDetailRequest> request);

    ApiResponse<ReviewDetailResponseDeleteAt> trash(Integer reviewDetailId);

    ApiResponse<ReviewDetailResponseDeleteAt> restore(Integer reviewDetailId);

    ApiResponse<Boolean> delete(Integer reviewDetailId);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
