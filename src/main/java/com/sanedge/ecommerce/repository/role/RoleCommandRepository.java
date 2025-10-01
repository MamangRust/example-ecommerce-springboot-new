package com.sanedge.ecommerce.repository.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.Role;

@Repository
public interface RoleCommandRepository extends JpaRepository<Role, Long>, RoleCommandRepositoryCustom {
}
