package com.sanedge.ecommerce.repository.shippingaddress;

import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.ShippingAddress;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class ShippingAddressCommandRepositoryImpl implements ShippingAddressCommandRepositoryCustom {
        @PersistenceContext
        private EntityManager em;

        @Override
        @Transactional
        public ShippingAddress trashed(Long shippingAddressId) {
                return (ShippingAddress) em.createNativeQuery(
                                "UPDATE shipping_addresses SET deleted_at = CURRENT_TIMESTAMP " +
                                                "WHERE shipping_address_id = :id AND deleted_at IS NULL " +
                                                "RETURNING *",
                                ShippingAddress.class)
                                .setParameter("id", shippingAddressId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public ShippingAddress restore(Long shippingAddressId) {
                return (ShippingAddress) em.createNativeQuery(
                                "UPDATE shipping_addresses SET deleted_at = NULL " +
                                                "WHERE shipping_address_id = :id AND deleted_at IS NOT NULL " +
                                                "RETURNING *",
                                ShippingAddress.class)
                                .setParameter("id", shippingAddressId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public boolean deletePermanent(Long shippingAddressId) {
                int deleted = em.createNativeQuery(
                                "DELETE FROM shipping_addresses WHERE shipping_address_id = :id AND deleted_at IS NOT NULL")
                                .setParameter("id", shippingAddressId)
                                .executeUpdate();
                return deleted > 0;
        }

        @Override
        @Transactional
        public boolean restoreAllDeleted() {
                int updated = em.createNativeQuery(
                                "UPDATE shipping_addresses SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return updated > 0;
        }

        @Override
        @Transactional
        public boolean deleteAllDeleted() {
                int deleted = em.createNativeQuery(
                                "DELETE FROM shipping_addresses WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return deleted > 0;
        }
}