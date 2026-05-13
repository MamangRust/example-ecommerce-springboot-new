package com.sanedge.ecommerce.repository.slider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.sanedge.ecommerce.BaseIntegrationTest;
import com.sanedge.ecommerce.models.Slider;

public class SliderRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private SliderQueryRepository queryRepository;

    @Autowired
    private SliderCommandRepository commandRepository;

    @Test
    void shouldCreateAndQuerySlider() {
        Slider slider = new Slider();
        slider.setName("Welcome Banner");
        slider.setImage("welcome.jpg");

        Slider saved = commandRepository.save(slider);
        assertThat(saved.getSliderId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        Page<Slider> page = queryRepository.findSliders("Welcome", PageRequest.of(0, 10));
        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getName()).isEqualTo("Welcome Banner");
    }
}
