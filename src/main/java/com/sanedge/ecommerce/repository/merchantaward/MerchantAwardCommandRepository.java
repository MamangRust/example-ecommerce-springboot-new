package com.sanedge.ecommerce.repository.merchantaward;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.merchant.MerchantCertificationAndAward;

@Repository
public interface MerchantAwardCommandRepository
        extends JpaRepository<MerchantCertificationAndAward, Long>,
        MerchantAwardCommandRepositoryCustom {
}