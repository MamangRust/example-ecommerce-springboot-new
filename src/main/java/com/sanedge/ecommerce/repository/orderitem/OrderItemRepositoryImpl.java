package com.sanedge.ecommerce.repository.orderitem;

import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.OrderItem;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class OrderItemRepositoryImpl implements OrderItemRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    private static final String TRASHED_QUERY = """
            UPDATE order_items
            SET deleted_at = CURRENT_TIMESTAMP
            WHERE order_item_id = :orderItemId
              AND deleted_at IS NULL
            """;

    private static final String RESTORE_QUERY = """
            UPDATE order_items
            SET deleted_at = NULL
            WHERE order_item_id = :orderItemId
              AND deleted_at IS NOT NULL
            """;

    private static final String DELETE_PERMANENT_QUERY = """
            DELETE FROM order_items
            WHERE order_item_id = :orderItemId
              AND deleted_at IS NOT NULL
            """;

    private static final String RESTORE_ALL_QUERY = """
            UPDATE order_items
            SET deleted_at = NULL
            WHERE deleted_at IS NOT NULL
            """;

    private static final String DELETE_ALL_QUERY = """
            DELETE FROM order_items
            WHERE deleted_at IS NOT NULL
            """;

    @Override
    @Transactional
    public OrderItem trashed(Long orderItemId) {
        OrderItem orderItem = em.find(OrderItem.class, orderItemId);

        if (orderItem != null) {
            em.createNativeQuery(TRASHED_QUERY)
                    .setParameter("orderItemId", orderItemId)
                    .executeUpdate();

            em.refresh(orderItem);
        }

        return orderItem;
    }

    @Override
    @Transactional
    public OrderItem restore(Long orderItemId) {
        OrderItem orderItem = em.find(OrderItem.class, orderItemId);

        if (orderItem != null) {
            em.createNativeQuery(RESTORE_QUERY)
                    .setParameter("orderItemId", orderItemId)
                    .executeUpdate();

            em.refresh(orderItem);
        }

        return orderItem;
    }

    @Override
    @Transactional
    public OrderItem deletePermanent(Long orderItemId) {
        OrderItem orderItem = em.find(OrderItem.class, orderItemId);

        if (orderItem != null) {
            em.createNativeQuery(DELETE_PERMANENT_QUERY)
                    .setParameter("orderItemId", orderItemId)
                    .executeUpdate();

            em.detach(orderItem);
        }

        return orderItem;
    }

    @Override
    @Transactional
    public boolean restoreAllDeleted() {
        int updated = em.createNativeQuery(RESTORE_ALL_QUERY)
                .executeUpdate();

        return updated > 0;
    }

    @Override
    @Transactional
    public boolean deleteAllDeleted() {
        int deleted = em.createNativeQuery(DELETE_ALL_QUERY)
                .executeUpdate();

        return deleted > 0;
    }
}
