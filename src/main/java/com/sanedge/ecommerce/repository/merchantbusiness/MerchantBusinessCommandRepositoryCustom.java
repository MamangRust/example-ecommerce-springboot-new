package com.sanedge.ecommerce.repository.merchantbusiness;

import com.sanedge.ecommerce.models.merchant.MerchantBusinessInformation;

public interface MerchantBusinessCommandRepositoryCustom {
    MerchantBusinessInformation trashed(Integer merchantBusinessInfoId);

    MerchantBusinessInformation restore(Integer merchantBusinessInfoId);

    boolean deletePermanent(Integer merchantBusinessInfoId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}