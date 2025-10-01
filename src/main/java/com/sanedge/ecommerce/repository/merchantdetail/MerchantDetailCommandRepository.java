package com.sanedge.ecommerce.repository.merchantdetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.merchant.MerchantDetail;

@Repository
public interface MerchantDetailCommandRepository
        extends JpaRepository<MerchantDetail, Long>, MerchantDetailCommandRepositoryCustom {

}