package com.umust.dobonglife.domain.promotion.infrastructure.jpa;

import com.umust.dobonglife.domain.promotion.domain.entity.Preset;
import com.umust.dobonglife.global.common.constant.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PresetJpaRepository extends JpaRepository<Preset, Long> {
    Optional<Preset> findFirstByCategory(Category category);
}
