package com.umust.dobonglife.domain.coupon.domain.repository;

import com.umust.dobonglife.domain.preset.domain.Preset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresetRepository extends JpaRepository<Preset, Long> {
}
