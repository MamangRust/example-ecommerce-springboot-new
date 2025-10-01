package com.sanedge.ecommerce.repository.shippingaddress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.ShippingAddress;

@Repository
public interface ShippingAddressCommandRepository
                extends JpaRepository<ShippingAddress, Long>, ShippingAddressCommandRepositoryCustom {

}