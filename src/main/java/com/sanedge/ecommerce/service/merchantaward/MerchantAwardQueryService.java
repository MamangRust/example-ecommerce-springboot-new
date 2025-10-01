package com.sanedge.ecommerce.service.merchantaward;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.merchantaward.MerchantAwardResponse;
import com.sanedge.ecommerce.domain.responses.merchantaward.MerchantAwardResponseDeleteAt;

public interface MerchantAwardQueryService {
    ApiResponsePagination<List<MerchantAwardResponse>> findAll(FindAllMerchantRequest req);

    ApiResponsePagination<List<MerchantAwardResponseDeleteAt>> findByActive(FindAllMerchantRequest req);

    ApiResponsePagination<List<MerchantAwardResponseDeleteAt>> findByTrashed(FindAllMerchantRequest req);

    ApiResponse<MerchantAwardResponse> findById(Integer merchantAwardId);
}
