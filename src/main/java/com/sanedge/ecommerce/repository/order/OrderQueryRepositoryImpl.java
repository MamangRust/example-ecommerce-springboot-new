package com.sanedge.ecommerce.repository.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.OrderItem;
import com.sanedge.ecommerce.models.order.OrderRelation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class OrderQueryRepositoryImpl implements OrderQueryRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public OrderRelation findOrderRelations(Long orderId) {
        String sql = """
                    SELECT o.order_id, o.user_id, o.merchant_id, o.total_price,
                           oi.order_item_id, oi.product_id, oi.quantity, oi.price
                    FROM orders o
                    LEFT JOIN order_items oi ON o.order_id = oi.order_id AND oi.deleted_at IS NULL
                    WHERE o.order_id = :orderId
                      AND o.deleted_at IS NULL
                """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("orderId", orderId)
                .getResultList();

        if (rows.isEmpty()) {
            return null;
        }

        OrderRelation orderRelation = null;

        for (Object[] row : rows) {
            if (orderRelation == null) {
                orderRelation = new OrderRelation();
                orderRelation.setOrderId(((Number) row[0]).longValue());
                orderRelation.setUserId(((Number) row[1]).intValue());
                orderRelation.setMerchantId(((Number) row[2]).intValue());
                orderRelation.setTotalPrice(((Number) row[3]).intValue());
                orderRelation.setOrderItems(new ArrayList<>());
            }

            if (row[4] != null) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(((Number) row[4]).longValue());
                item.setOrderId(((Number) row[0]).intValue());
                item.setProductId(((Number) row[5]).intValue());
                item.setQuantity(((Number) row[6]).intValue());
                item.setPrice(((Number) row[7]).intValue());
                orderRelation.getOrderItems().add(item);
            }
        }

        return orderRelation;
    }
}
