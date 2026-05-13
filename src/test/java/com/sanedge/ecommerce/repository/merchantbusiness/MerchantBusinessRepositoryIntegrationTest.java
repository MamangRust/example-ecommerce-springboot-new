package com.sanedge.ecommerce.repository.merchantbusiness;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.merchant.MerchantBusinessInformation;

public class MerchantBusinessRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MerchantBusinessQueryRepository queryRepository;

    @Autowired
    private MerchantBusinessCommandRepository commandRepository;

    @Test
    void shouldCreateAndQueryMerchantBusiness() {
        MerchantBusinessInformation info = new MerchantBusinessInformation();
        info.setMerchantId(1);
        info.setBusinessType("Retail Store");
        info.setTaxId("12-345-678");
        info.setEstablishedYear(2010);
        info.setNumberOfEmployees(12);

        MerchantBusinessInformation saved = commandRepository.save(info);
        assertThat(saved.getMerchantBusinessInfoId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        Page<MerchantBusinessInformation> page = queryRepository.findMerchantBusinessInformation("Retail", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getBusinessType()).contains("Retail");

        Optional<MerchantBusinessInformation> found = queryRepository.findMerchantBusinessInformationById(saved.getMerchantBusinessInfoId().intValue());
        assertThat(found).isPresent();
    }
}
