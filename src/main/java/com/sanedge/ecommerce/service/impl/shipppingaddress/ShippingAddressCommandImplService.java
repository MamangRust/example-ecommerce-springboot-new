package com.sanedge.ecommerce.service.impl.shipppingaddress;

import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.shipping.ShippingAddressResponseDeleteAt;
import com.sanedge.ecommerce.models.ShippingAddress;
import com.sanedge.ecommerce.repository.shippingaddress.ShippingAddressCommandRepository;
import com.sanedge.ecommerce.service.shippingaddres.ShippingAddressCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShippingAddressCommandImplService implements ShippingAddressCommand {

    private final ShippingAddressCommandRepository shippingAddressCommandRepository;

    @Override
    public ApiResponse<ShippingAddressResponseDeleteAt> trash(Integer shippingId) {
        log.info("🗑️ Trashing shipping address id={}", shippingId);
        try {
            ShippingAddress shippingAddress = shippingAddressCommandRepository.trashed(shippingId.longValue());
            return ApiResponse.<ShippingAddressResponseDeleteAt>builder()
                    .status("success")
                    .message("🗑️ Shipping address trashed successfully!")
                    .data(ShippingAddressResponseDeleteAt.from(shippingAddress))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to trash shipping address id={}", shippingId, e);
            return ApiResponse.<ShippingAddressResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to trash shipping address: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<ShippingAddressResponseDeleteAt> restore(Integer shippingId) {
        log.info("♻️ Restoring shipping address id={}", shippingId);
        try {
            ShippingAddress shippingAddress = shippingAddressCommandRepository.restore(shippingId.longValue());
            return ApiResponse.<ShippingAddressResponseDeleteAt>builder()
                    .status("success")
                    .message("♻️ Shipping address restored successfully!")
                    .data(ShippingAddressResponseDeleteAt.from(shippingAddress))
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore shipping address id={}", shippingId, e);
            return ApiResponse.<ShippingAddressResponseDeleteAt>builder()
                    .status("error")
                    .message("Failed to restore shipping address: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deletePermanently(Integer shippingId) {
        log.info("🧨 Permanently deleting shipping address id={}", shippingId);
        try {
            shippingAddressCommandRepository.deletePermanent(shippingId.longValue());
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🧨 Shipping address permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to permanently delete shipping address id={}", shippingId, e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to permanently delete shipping address: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> restoreAll() {
        log.info("🔄 Restoring ALL trashed shipping addresses");
        try {
            shippingAddressCommandRepository.restoreAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("🔄 All shipping addresses restored successfully!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to restore all shipping addresses", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to restore all shipping addresses: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }

    @Override
    public ApiResponse<Boolean> deleteAllPermanent() {
        log.info("💣 Permanently deleting ALL trashed shipping addresses");
        try {
            shippingAddressCommandRepository.deleteAllDeleted();
            return ApiResponse.<Boolean>builder()
                    .status("success")
                    .message("💣 All shipping addresses permanently deleted!")
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("💥 Failed to delete all shipping addresses", e);
            return ApiResponse.<Boolean>builder()
                    .status("error")
                    .message("Failed to delete all shipping addresses: " + e.getMessage())
                    .data(false)
                    .build();
        }
    }
}
