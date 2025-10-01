package com.sanedge.ecommerce.repository.merchantbusiness;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.merchant.MerchantBusinessInformation;

@Repository
public interface MerchantBusinessCommandRepository
                extends JpaRepository<MerchantBusinessInformation, Long>,
                MerchantBusinessCommandRepositoryCustom {
}
