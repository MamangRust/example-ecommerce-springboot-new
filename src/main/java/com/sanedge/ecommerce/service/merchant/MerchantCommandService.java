package com.sanedge.ecommerce.service.merchant;

import com.sanedge.ecommerce.domain.requests.merchant.CreateMerchantRequest;
import com.sanedge.ecommerce.domain.requests.merchant.UpdateMerchantRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.merchant.MerchantResponse;
import com.sanedge.ecommerce.domain.responses.merchant.MerchantResponseDeleteAt;

public interface MerchantCommandService {
    ApiResponse<MerchantResponse> createMerchant(CreateMerchantRequest req);

    ApiResponse<MerchantResponse> updateMerchant(UpdateMerchantRequest req);

    ApiResponse<MerchantResponseDeleteAt> trashedMerchant(Integer merchantId);

    ApiResponse<MerchantResponseDeleteAt> restoreMerchant(Integer merchantId);

    ApiResponse<Boolean> deleteMerchantPermanent(Integer merchantId);

    ApiResponse<Boolean> restoreAllMerchant();

    ApiResponse<Boolean> deleteAllMerchantPermanent();
}
