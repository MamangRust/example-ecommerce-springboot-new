package com.sanedge.ecommerce.repository.orderitem;

import com.sanedge.ecommerce.models.OrderItem;

public interface OrderItemRepositoryCustom {
    OrderItem trashed(Long orderItemId);

    OrderItem restore(Long orderItemId);

    OrderItem deletePermanent(Long orderItemId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
