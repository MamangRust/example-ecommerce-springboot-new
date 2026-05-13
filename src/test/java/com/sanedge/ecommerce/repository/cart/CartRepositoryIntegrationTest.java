package com.sanedge.ecommerce.repository.cart;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.Cart;

public class CartRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CartQueryRepository cartQueryRepository;

    @Autowired
    private CartCommandRepository cartCommandRepository;

    @Test
    void shouldCreateAndQueryAndPageCart() {
        Cart cart = new Cart();
        cart.setUserId(regularUser.getUserId().intValue());
        cart.setProductId(12345);
        cart.setName("Super Smart Laptop");
        cart.setPrice(1500000);
        cart.setImage("laptop.png");
        cart.setQuantity(2);
        cart.setWeight(1500);
        
        Cart saved = cartCommandRepository.save(cart);
        assertThat(saved.getCartId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        Page<Cart> page = cartQueryRepository.findCartsByUser(regularUser.getUserId().intValue(), "Smart", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getName()).contains("Smart");

        cartCommandRepository.deleteCartById(saved.getCartId());
        entityManager.flush();
        entityManager.clear();

        assertThat(cartCommandRepository.findById(saved.getCartId())).isEmpty();
    }
}
