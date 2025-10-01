package com.sanedge.ecommerce.service.merchantpolicy;

import com.sanedge.ecommerce.domain.requests.merchantpolicy.CreateMerchantPolicyRequest;
import com.sanedge.ecommerce.domain.requests.merchantpolicy.UpdateMerchantPolicyRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.merchantpolicy.MerchantPoliciesResponse;
import com.sanedge.ecommerce.domain.responses.merchantpolicy.MerchantPoliciesResponseDeleteAt;

public interface MerchantPolicyCommandService {
    ApiResponse<MerchantPoliciesResponse> create(CreateMerchantPolicyRequest request);

    ApiResponse<MerchantPoliciesResponse> update(UpdateMerchantPolicyRequest request);

    ApiResponse<MerchantPoliciesResponseDeleteAt> trash(Integer id);

    ApiResponse<MerchantPoliciesResponseDeleteAt> restore(Integer id);

    ApiResponse<Boolean> delete(Integer id);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAll();
}
