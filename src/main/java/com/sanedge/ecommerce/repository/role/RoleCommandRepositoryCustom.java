package com.sanedge.ecommerce.repository.role;

import com.sanedge.ecommerce.models.Role;

public interface RoleCommandRepositoryCustom {
    Role trashed(Long roleId);

    Role restore(Long roleId);

    Role deletePermanent(Long roleId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
