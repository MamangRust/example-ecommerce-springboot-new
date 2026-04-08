package com.sanedge.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.ecommerce.domain.requests.shipping.FindAllShippingAddress;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.shipping.ShippingAddressResponse;
import com.sanedge.ecommerce.domain.responses.shipping.ShippingAddressResponseDeleteAt;
import com.sanedge.ecommerce.service.shippingaddres.ShippingAddressCommand;
import com.sanedge.ecommerce.service.shippingaddres.ShippingAddressQueryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shipping-address")
public class ShippingAddressController {

    private final ShippingAddressQueryService shippingQueryService;
    private final ShippingAddressCommand shippingCommandService;

    @GetMapping("")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponsePagination<List<ShippingAddressResponse>>> findAll(
            @Valid FindAllShippingAddress req) {
        return ResponseEntity.ok(shippingQueryService.findAll(req));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<ShippingAddressResponseDeleteAt>>> findByActive(
            @Valid FindAllShippingAddress req) {
        return ResponseEntity.ok(shippingQueryService.findByActive(req));
    }

    @GetMapping("/trashed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponsePagination<List<ShippingAddressResponseDeleteAt>>> findByTrashed(
            @Valid FindAllShippingAddress req) {
        return ResponseEntity.ok(shippingQueryService.findByTrashed(req));
    }

    @GetMapping("/{shippingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<ShippingAddressResponse>> findById(@PathVariable Integer shippingId) {
        return ResponseEntity.ok(shippingQueryService.findById(shippingId));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public ResponseEntity<ApiResponse<ShippingAddressResponse>> findByOrder(@PathVariable Integer orderId) {
        return ResponseEntity.ok(shippingQueryService.findByOrder(orderId));
    }

    @PostMapping("/trashed/{shippingId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShippingAddressResponseDeleteAt>> trash(@PathVariable Integer shippingId) {
        return ResponseEntity.ok(shippingCommandService.trash(shippingId));
    }

    @PostMapping("/restore/{shippingId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShippingAddressResponseDeleteAt>> restore(@PathVariable Integer shippingId) {
        return ResponseEntity.ok(shippingCommandService.restore(shippingId));
    }

    @DeleteMapping("/permanent/{shippingId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deletePermanent(@PathVariable Integer shippingId) {
        return ResponseEntity.ok(shippingCommandService.deletePermanently(shippingId));
    }

    @PostMapping("/restore/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> restoreAll() {
        return ResponseEntity.ok(shippingCommandService.restoreAll());
    }

    @PostMapping("/permanent/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllPermanent() {
        return ResponseEntity.ok(shippingCommandService.deleteAllPermanent());
    }
}
