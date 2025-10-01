package com.sanedge.ecommerce.service.merchant;

import java.util.List;

import com.sanedge.ecommerce.domain.requests.merchant.FindAllMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.merchant.MerchantResponse;
import com.sanedge.ecommerce.domain.responses.merchant.MerchantResponseDeleteAt;

public interface MerchantQueryService {
    ApiResponsePagination<List<MerchantResponse>> findAll(FindAllMerchantRequest req);

    ApiResponsePagination<List<MerchantResponseDeleteAt>> findByActive(FindAllMerchantRequest req);

    ApiResponsePagination<List<MerchantResponseDeleteAt>> findByTrashed(FindAllMerchantRequest req);

    ApiResponse<MerchantResponse> findById(Integer merchantId);

    ApiResponse<MerchantResponse> findByUserId(Integer userId);
}
