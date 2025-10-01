package com.sanedge.ecommerce.repository.banner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.Banner;

@Repository
public interface BannerCommandRepository extends JpaRepository<Banner, Long>, BannerCommandRepositoryCustom {

}