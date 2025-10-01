package com.sanedge.ecommerce.repository.slider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sanedge.ecommerce.models.Slider;

@Repository
public interface SliderCommandRepository extends JpaRepository<Slider, Long>, SliderCommandRepositoryCustom {
}
