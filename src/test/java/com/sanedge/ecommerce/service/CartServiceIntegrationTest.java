package com.sanedge.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.domain.requests.cart.CreateCartRequest;
import com.sanedge.ecommerce.domain.requests.cart.DeleteCartRequest;
import com.sanedge.ecommerce.domain.requests.cart.FindAllCartsRequest;
import com.sanedge.ecommerce.domain.responses.api.ApiResponse;
import com.sanedge.ecommerce.domain.responses.api.ApiResponsePagination;
import com.sanedge.ecommerce.domain.responses.cart.CartResponse;
import com.sanedge.ecommerce.models.Cart;
import com.sanedge.ecommerce.repository.cart.CartCommandRepository;

public class CartServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartCommandRepository cartCommandRepository;

    @Test
    void shouldAddCartsAndFindAndThenDelete() {
        // Setup via direct repository
        Cart cart = new Cart();
        cart.setUserId(regularUser.getUserId().intValue());
        cart.setProductId(555);
        cart.setName("Gaming Keyboard");
        cart.setPrice(450000);
        cart.setImage("keyboard.png");
        cart.setQuantity(1);
        cart.setWeight(500);
        cartCommandRepository.save(cart);

        entityManager.flush();
        entityManager.clear();

        // 1. Find all
        FindAllCartsRequest findReq = new FindAllCartsRequest();
        findReq.setUserId(regularUser.getUserId().intValue());
        findReq.setSearch("Keyboard");
        findReq.setPage(1);
        findReq.setPageSize(10);

        ApiResponsePagination<List<CartResponse>> cartsResponse = cartService.findAll(findReq);
        assertThat(cartsResponse.getStatus()).isEqualTo("success");
        assertThat(cartsResponse.getData()).isNotEmpty();
        assertThat(cartsResponse.getData().get(0).getName()).isEqualTo("Gaming Keyboard");

        // 2. Create Cart
        CreateCartRequest createReq = new CreateCartRequest();
        createReq.setUserId(regularUser.getUserId().intValue());
        createReq.setProductId(666);
        createReq.setQuantity(3);

        ApiResponse<CartResponse> createResp = cartService.createCart(createReq);
        assertThat(createResp.getStatus()).isEqualTo("success");
        assertThat(createResp.getData().getProductId()).isEqualTo(666);

        // 3. Delete All
        DeleteCartRequest deleteReq = new DeleteCartRequest();
        deleteReq.setCartIds(List.of(cart.getCartId().intValue(), createResp.getData().getId().intValue()));

        ApiResponse<Boolean> deleteResp = cartService.deleteAllPermanently(deleteReq);
        assertThat(deleteResp.getStatus()).isEqualTo("success");
        assertThat(deleteResp.getData()).isTrue();
    }
}
