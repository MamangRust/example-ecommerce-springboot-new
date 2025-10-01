package com.sanedge.ecommerce.repository.merchant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.merchant.Merchant;

@Repository
public interface MerchantCommandRepository extends JpaRepository<Merchant, Long>, MerchantCommandRepositoryCustom {

}
