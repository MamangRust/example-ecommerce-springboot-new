package com.sanedge.ecommerce.repository.shippingaddress;

import com.sanedge.ecommerce.models.ShippingAddress;

public interface ShippingAddressCommandRepositoryCustom {
    ShippingAddress trashed(Long shippingAddressId);

    ShippingAddress restore(Long shippingAddressId);

    boolean deletePermanent(Long shippingAddressId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}