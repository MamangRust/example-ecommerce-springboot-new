package com.sanedge.ecommerce.service.shippingaddres;

import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.shipping.ShippingAddressResponseDeleteAt;

public interface ShippingAddressCommand {
    ApiResponse<ShippingAddressResponseDeleteAt> trash(Integer shippingId);

    ApiResponse<ShippingAddressResponseDeleteAt> restore(Integer shippingId);

    ApiResponse<Boolean> deletePermanently(Integer shippingId);

    ApiResponse<Boolean> restoreAll();

    ApiResponse<Boolean> deleteAllPermanent();
}
