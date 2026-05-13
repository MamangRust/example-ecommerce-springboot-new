package com.sanedge.ecommerce.repository.merchantaward;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.merchant.MerchantCertificationAndAward;

public class MerchantAwardRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MerchantAwardQueryRepository queryRepository;

    @Autowired
    private MerchantAwardCommandRepository commandRepository;

    @Test
    void shouldCreateAndQueryMerchantAward() {
        MerchantCertificationAndAward award = new MerchantCertificationAndAward();
        award.setMerchantId(1);
        award.setTitle("ISO 9001 Award");
        award.setDescription("First test award");
        award.setIssuedBy("ISO Organization");

        MerchantCertificationAndAward saved = commandRepository.save(award);
        assertThat(saved.getMerchantCertificationId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        Page<MerchantCertificationAndAward> awards = queryRepository.findMerchantAwards("ISO", PageRequest.of(0, 10));
        assertThat(awards.getContent()).isNotEmpty();
        assertThat(awards.getContent().get(0).getTitle()).contains("ISO");

        Optional<MerchantCertificationAndAward> found = queryRepository.findMerchantAwardById(saved.getMerchantCertificationId());
        assertThat(found).isPresent();
    }
}
