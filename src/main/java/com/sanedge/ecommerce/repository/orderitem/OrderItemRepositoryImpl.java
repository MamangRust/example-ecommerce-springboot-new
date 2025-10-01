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

        @Override
        @Transactional
        public OrderItem trashed(Long orderItemId) {
                return (OrderItem) em.createNativeQuery(
                                "UPDATE order_items SET deleted_at = CURRENT_TIMESTAMP " +
                                                "WHERE order_item_id = :orderItemId AND deleted_at IS NULL " +
                                                "RETURNING *",
                                OrderItem.class)
                                .setParameter("orderItemId", orderItemId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public OrderItem restore(Long orderItemId) {
                return (OrderItem) em.createNativeQuery(
                                "UPDATE order_items SET deleted_at = NULL " +
                                                "WHERE order_item_id = :orderItemId AND deleted_at IS NOT NULL " +
                                                "RETURNING *",
                                OrderItem.class)
                                .setParameter("orderItemId", orderItemId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public OrderItem deletePermanent(Long orderItemId) {
                return (OrderItem) em.createNativeQuery(
                                "DELETE FROM order_items " +
                                                "WHERE order_item_id = :orderItemId AND deleted_at IS NOT NULL " +
                                                "RETURNING *",
                                OrderItem.class)
                                .setParameter("orderItemId", orderItemId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public boolean restoreAllDeleted() {
                int updated = em.createNativeQuery(
                                "UPDATE order_items SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return updated > 0;
        }

        @Override
        @Transactional
        public boolean deleteAllDeleted() {
                int deleted = em.createNativeQuery(
                                "DELETE FROM order_items WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return deleted > 0;
        }
}
