package com.sanedge.ecommerce.repository.user_role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

}
