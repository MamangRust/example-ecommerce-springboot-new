package com.sanedge.ecommerce.repository.merchantpolicy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.merchant.MerchantPolicy;

public class MerchantPolicyRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MerchantPolicyQueryRepository queryRepository;

    @Autowired
    private MerchantPolicyCommandRepository commandRepository;

    @Test
    void shouldCreateAndQueryMerchantPolicy() {
        MerchantPolicy policy = new MerchantPolicy();
        policy.setMerchantId(1);
        policy.setPolicyType("Return");
        policy.setTitle("Return Policy");
        policy.setDescription("Returns within 30 days");

        MerchantPolicy saved = commandRepository.save(policy);
        assertThat(saved.getMerchantPolicyId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        Page<MerchantPolicy> page = queryRepository.findMerchantPolicies("Return", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getPolicyType()).isEqualTo("Return");

        Optional<MerchantPolicy> found = queryRepository.findMerchantPolicyById(saved.getMerchantPolicyId());
        assertThat(found).isPresent();
    }
}
