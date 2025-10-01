package com.sanedge.ecommerce.service.merchantpolicy;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.merchantpolicy.MerchantPoliciesResponse;
import com.sanedge.ecommerce.domain.responses.merchantpolicy.MerchantPoliciesResponseDeleteAt;

public interface MerchantPolicyQueryService {
    ApiResponsePagination<List<MerchantPoliciesResponse>> findAll(FindAllMerchantRequest req);

    ApiResponsePagination<List<MerchantPoliciesResponseDeleteAt>> findByActive(FindAllMerchantRequest req);

    ApiResponsePagination<List<MerchantPoliciesResponseDeleteAt>> findByTrashed(FindAllMerchantRequest req);

    ApiResponse<MerchantPoliciesResponse> findById(Integer id);
}
