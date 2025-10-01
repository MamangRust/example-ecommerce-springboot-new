package com.sanedge.ecommerce.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.category.Category;

@Repository
public interface CategoryCommandRepository extends JpaRepository<Category, Long>, CategoryCommandRepositoryCustom {

}
