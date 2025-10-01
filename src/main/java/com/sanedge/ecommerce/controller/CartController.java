package com.sanedge.ecommerce.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanedge.ecommerce.domain.requests.cart.CreateCartRequest;
import com.sanedge.ecommerce.domain.requests.cart.DeleteCartRequest;
import com.sanedge.ecommerce.domain.requests.cart.FindAllCartsRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.cart.CartResponse;
import com.sanedge.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponsePagination<List<CartResponse>>> findAll(
            @ModelAttribute FindAllCartsRequest req) {

        return ResponseEntity.ok(cartService.findAll(req));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CartResponse>> createCart(@Valid @RequestBody CreateCartRequest req) {
        return ResponseEntity.ok(cartService.createCart(req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteCart(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.deletePermanent(id));
    }

    @PostMapping("/delete-all")
    public ResponseEntity<ApiResponse<Boolean>> deleteAllCarts(@Valid @RequestBody DeleteCartRequest req) {
        return ResponseEntity.ok(cartService.deleteAllPermanently(req));
    }
}
