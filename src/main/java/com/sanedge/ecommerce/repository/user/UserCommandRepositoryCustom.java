package com.sanedge.ecommerce.repository.user;

import com.sanedge.ecommerce.models.User;

public interface UserCommandRepositoryCustom {
    User trashed(Long userId);

    User restore(Long userId);
}
