package com.sanedge.ecommerce.repository.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.order.Order;

public class OrderRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderQueryRepository queryRepository;

    @Autowired
    private OrderCommandRepository commandRepository;

    @Test
    void shouldCreateAndQueryOrder() {
        Order order = new Order();
        order.setUserId(adminUser.getUserId().intValue());
        order.setMerchantId(1);
        order.setTotalPrice(250000);

        Order saved = commandRepository.save(order);
        assertThat(saved.getOrderId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        Page<Order> page = queryRepository.findOrders("250000", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getTotalPrice()).isEqualTo(250000);

        Optional<Order> found = queryRepository.findOrderById(saved.getOrderId());
        assertThat(found).isPresent();
    }
}
