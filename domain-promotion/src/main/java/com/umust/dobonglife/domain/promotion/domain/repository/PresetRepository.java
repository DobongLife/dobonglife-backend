package com.umust.dobonglife.domain.promotion.domain.repository;

import com.umust.dobonglife.domain.promotion.domain.entity.Preset;
import com.umust.dobonglife.global.common.constant.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PresetRepository extends JpaRepository<Preset, Long> {
    Optional<Preset> findFirstByCategory(Category category);
}
