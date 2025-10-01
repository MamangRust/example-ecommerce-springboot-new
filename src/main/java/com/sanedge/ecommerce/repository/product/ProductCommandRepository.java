package com.sanedge.ecommerce.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.Product;

@Repository
public interface ProductCommandRepository extends JpaRepository<Product, Long>, ProductCommandRepositoryCustom {
}