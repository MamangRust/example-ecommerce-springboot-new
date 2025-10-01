package com.sanedge.ecommerce.service.merchantdetail;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailRelationResponse;
import com.sanedge.ecommerce.domain.responses.merchantdetail.MerchantDetailRelationResponseDeleteAt;

public interface MerchantDetailQueryService {
    ApiResponsePagination<List<MerchantDetailRelationResponse>> findAll(FindAllMerchantRequest req);

    ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>> findByActive(FindAllMerchantRequest req);

    ApiResponsePagination<List<MerchantDetailRelationResponseDeleteAt>> findByTrashed(FindAllMerchantRequest req);

    ApiResponse<MerchantDetailRelationResponse> findById(Integer merchantID);
}
