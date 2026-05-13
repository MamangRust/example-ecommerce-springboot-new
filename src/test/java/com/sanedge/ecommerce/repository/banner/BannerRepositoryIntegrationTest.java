package com.sanedge.ecommerce.repository.banner;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.sql.Time;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.Banner;

public class BannerRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private BannerQueryRepository bannerQueryRepository;

    @Autowired
    private BannerCommandRepository bannerCommandRepository;

    @Test
    void shouldCreateAndQueryBanner() {
        Banner banner = new Banner();
        banner.setName("UniqueBannerSale");
        banner.setStartDate(Date.valueOf("2026-01-01"));
        banner.setEndDate(Date.valueOf("2026-12-31"));
        banner.setStartTime(Time.valueOf("00:00:00"));
        banner.setEndTime(Time.valueOf("23:59:59"));
        banner.setIsActive(true);

        Banner saved = bannerCommandRepository.save(banner);
        assertThat(saved.getBannerId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        Page<Banner> page = bannerQueryRepository.findBanners("UniqueBannerSale", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getName()).isEqualTo("UniqueBannerSale");

        Optional<Banner> found = bannerQueryRepository.findByName("UniqueBannerSale");
        assertThat(found).isPresent();
    }
}
