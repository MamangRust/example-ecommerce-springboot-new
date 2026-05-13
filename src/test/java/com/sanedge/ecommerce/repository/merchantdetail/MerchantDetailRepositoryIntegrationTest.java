package com.sanedge.ecommerce.repository.merchantdetail;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.merchant.MerchantDetail;

public class MerchantDetailRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MerchantDetailQueryRepository queryRepository;

    @Autowired
    private MerchantDetailCommandRepository commandRepository;

    @Test
    void shouldCreateAndQueryMerchantDetail() {
        MerchantDetail detail = new MerchantDetail();
        detail.setMerchantId(1);
        detail.setDisplayName("Great Merchant");
        detail.setShortDescription("Short desc");
        detail.setWebsiteUrl("https://great-merch.com");
        detail.setCoverImageUrl("cover.jpg");
        detail.setLogoUrl("logo.jpg");

        MerchantDetail saved = commandRepository.save(detail);
        assertThat(saved.getMerchantDetailId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        Optional<MerchantDetail> found = queryRepository.findById(saved.getMerchantDetailId());
        assertThat(found).isPresent();
        assertThat(found.get().getDisplayName()).isEqualTo("Great Merchant");
    }
}
