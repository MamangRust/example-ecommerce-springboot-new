package com.sanedge.ecommerce.service.merchantbusiness;

import com.sanedge.ecommerce.domain.requests.merchantbusiness.CreateMerchantBusinessRequest;
import com.sanedge.ecommerce.domain.requests.merchantbusiness.UpdateMerchantBusinessRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.merchantbusiness.MerchantBusinessResponse;
import com.sanedge.ecommerce.domain.responses.merchantbusiness.MerchantBusinessResponseDeleteAt;

public interface MerchantBusinessCommandService {
    ApiResponse<MerchantBusinessResponse> createMerchantBusiness(CreateMerchantBusinessRequest req);

    ApiResponse<MerchantBusinessResponse> updateMerchantBusiness(UpdateMerchantBusinessRequest req);

    ApiResponse<MerchantBusinessResponseDeleteAt> trashedMerchantBusiness(Integer merchantBusinessInfoId);

    ApiResponse<MerchantBusinessResponseDeleteAt> restoreMerchantBusiness(Integer merchantBusinessInfoId);

    ApiResponse<Boolean> deleteMerchantBusinessPermanent(Integer merchantBusinessInfoId);

    ApiResponse<Boolean> restoreAllMerchantBusiness();

    ApiResponse<Boolean> deleteAllMerchantBusinessPermanent();
}