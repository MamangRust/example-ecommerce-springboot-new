package com.sanedge.ecommerce.service.merchantbusiness;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.merchantbusiness.MerchantBusinessResponse;
import com.sanedge.ecommerce.domain.responses.merchantbusiness.MerchantBusinessResponseDeleteAt;

public interface MerchantBusinessQueryService {
    ApiResponsePagination<List<MerchantBusinessResponse>> findAll(FindAllMerchantRequest req);

    ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>> findByActive(FindAllMerchantRequest req);

    ApiResponsePagination<List<MerchantBusinessResponseDeleteAt>> findByTrashed(FindAllMerchantRequest req);

    ApiResponse<MerchantBusinessResponse> findById(Integer merchantId);
}