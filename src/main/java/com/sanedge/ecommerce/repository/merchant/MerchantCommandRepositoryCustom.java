package com.sanedge.ecommerce.repository.merchant;

import com.sanedge.ecommerce.models.merchant.Merchant;

public interface MerchantCommandRepositoryCustom {
    Merchant trashed(Long merchantId);

    Merchant restore(Long merchantId);

    boolean deletePermanent(Long merchantId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}