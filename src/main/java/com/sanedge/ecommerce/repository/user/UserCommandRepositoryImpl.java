package com.sanedge.ecommerce.repository.user;

import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class UserCommandRepositoryImpl implements UserCommandRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    private static final String TRASHED_QUERY = """
            UPDATE users
            SET deleted_at = CURRENT_TIMESTAMP
            WHERE id = :userId
              AND deleted_at IS NULL
            """;

    private static final String RESTORE_QUERY = """
            UPDATE users
            SET deleted_at = NULL
            WHERE id = :userId
              AND deleted_at IS NOT NULL
            """;

    @Override
    @Transactional
    public User trashed(Long userId) {
        User user = em.find(User.class, userId);

        if (user != null) {
            em.createNativeQuery(TRASHED_QUERY)
                    .setParameter("userId", userId)
                    .executeUpdate();

            em.refresh(user);
        }

        return user;
    }

    @Override
    @Transactional
    public User restore(Long userId) {
        User user = em.find(User.class, userId);

        if (user != null) {
            em.createNativeQuery(RESTORE_QUERY)
                    .setParameter("userId", userId)
                    .executeUpdate();

            em.refresh(user);
        }

        return user;
    }
}
