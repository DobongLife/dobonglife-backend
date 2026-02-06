package com.umust.dobonglife.domain.coupon.domain.repository;

import com.umust.dobonglife.domain.preset.domain.Preset;
import com.umust.dobonglife.global.common.model.constant.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface PresetRepository extends JpaRepository<Preset, Long> {
    List<Preset> findByCategory(Category category);
}
