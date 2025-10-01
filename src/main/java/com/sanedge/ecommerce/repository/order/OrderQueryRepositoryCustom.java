package com.sanedge.ecommerce.repository.order;

import com.sanedge.ecommerce.models.order.OrderRelation;

public interface OrderQueryRepositoryCustom {
    OrderRelation findOrderRelations(Long orderId);
}
