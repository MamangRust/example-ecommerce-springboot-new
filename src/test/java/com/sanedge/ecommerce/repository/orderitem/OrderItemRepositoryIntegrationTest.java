package com.sanedge.ecommerce.repository.orderitem;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.OrderItem;

public class OrderItemRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    void shouldFindAllAndTrashAndRestoreAndPermanentDeleteOrderItem() {
        OrderItem item = new OrderItem();
        item.setOrderId(1);
        item.setProductId(11);
        item.setQuantity(5);
        item.setPrice(100);
        item = orderItemRepository.save(item);

        entityManager.flush();
        entityManager.clear();

        List<OrderItem> items = orderItemRepository.findOrderItemByOrder(1L);
        assertThat(items).isNotEmpty();

        OrderItem trashed = orderItemRepository.trashed(item.getOrderItemId());
        assertThat(trashed).isNotNull();

        OrderItem restored = orderItemRepository.restore(item.getOrderItemId());
        assertThat(restored).isNotNull();

        OrderItem deletedItem = orderItemRepository.deletePermanent(item.getOrderItemId());
        assertThat(deletedItem).isNotNull();
    }
}
