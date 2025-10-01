package com.sanedge.ecommerce.repository.merchantpolicy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.merchant.MerchantPolicy;

@Repository
public interface MerchantPolicyCommandRepository
                extends JpaRepository<MerchantPolicy, Long>, MerchantPolicyCommandRepositoryCustom {

}