package com.sanedge.ecommerce.repository.order;

import com.sanedge.ecommerce.models.order.Order;

public interface OrderCommandRepositoryCustom {
    Order trashed(Long orderId);

    Order restore(Long orderId);

    Order deletePermanent(Long orderId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
