package com.sanedge.ecommerce.repository.merchantsociallink;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.merchant.MerchantSocialMediaLink;

public class MerchantSocialLinkRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MerchantSocialMediaLinkRepository repository;

    @Test
    void shouldCreateAndQueryMerchantSocialLink() {
        MerchantSocialMediaLink link = new MerchantSocialMediaLink();
        link.setMerchantDetailId(1);
        link.setPlatform("Facebook");
        link.setUrl("https://facebook.com/great");

        MerchantSocialMediaLink saved = repository.save(link);
        assertThat(saved.getMerchantSocialId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        Optional<MerchantSocialMediaLink> found = repository.findById(saved.getMerchantSocialId());
        assertThat(found).isPresent();
        assertThat(found.get().getPlatform()).isEqualTo("Facebook");
    }
}
