package com.sanedge.ecommerce.repository.shippingaddress;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.ShippingAddress;

public class ShippingAddressRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ShippingAddressQueryRepository queryRepository;

    @Autowired
    private ShippingAddressCommandRepository commandRepository;

    @Test
    void shouldCreateAndQueryShippingAddress() {
        ShippingAddress address = new ShippingAddress();
        address.setOrderId(100);
        address.setAlamat("Jl. Merdeka No. 1");
        address.setProvinsi("Jakarta");
        address.setNegara("Indonesia");
        address.setKota("Jakarta Pusat");
        address.setCourier("JNE");
        address.setShippingMethod("REG");
        address.setShippingCost(15000);

        ShippingAddress saved = commandRepository.save(address);
        assertThat(saved.getShippingAddressId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        Page<ShippingAddress> page = queryRepository.findShippingAddresses("Jakarta", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();

        Optional<ShippingAddress> found = queryRepository.findByOrderId(100);
        assertThat(found).isPresent();
    }
}
