package com.sanedge.ecommerce.repository.slider;

import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.Slider;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class SliderCommandRepositoryImpl implements SliderCommandRepositoryCustom {

        @PersistenceContext
        private EntityManager em;

        @Override
        @Transactional
        public Slider trashed(Long sliderId) {
                return (Slider) em.createNativeQuery(
                                "UPDATE sliders SET deleted_at = CURRENT_TIMESTAMP " +
                                                "WHERE slider_id = :sliderId AND deleted_at IS NULL " +
                                                "RETURNING *",
                                Slider.class)
                                .setParameter("sliderId", sliderId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public Slider restore(Long sliderId) {
                return (Slider) em.createNativeQuery(
                                "UPDATE sliders SET deleted_at = NULL " +
                                                "WHERE slider_id = :sliderId AND deleted_at IS NOT NULL " +
                                                "RETURNING *",
                                Slider.class)
                                .setParameter("sliderId", sliderId)
                                .getSingleResult();
        }

        @Override
        @Transactional
        public boolean deletePermanent(Long sliderId) {
                int deleted = em.createNativeQuery(
                                "DELETE FROM sliders WHERE slider_id = :sliderId AND deleted_at IS NOT NULL")
                                .setParameter("sliderId", sliderId)
                                .executeUpdate();
                return deleted > 0;
        }

        @Override
        @Transactional
        public boolean restoreAllDeleted() {
                int updated = em.createNativeQuery(
                                "UPDATE sliders SET deleted_at = NULL WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return updated > 0;
        }

        @Override
        @Transactional
        public boolean deleteAllDeleted() {
                int deleted = em.createNativeQuery(
                                "DELETE FROM sliders WHERE deleted_at IS NOT NULL")
                                .executeUpdate();
                return deleted > 0;
        }
}
