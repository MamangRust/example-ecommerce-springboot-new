package com.sanedge.ecommerce.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.order.Order;

@Repository
public interface OrderCommandRepository extends
        JpaRepository<Order, Long>,
        OrderCommandRepositoryCustom {
}