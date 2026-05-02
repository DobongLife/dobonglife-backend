package com.umust.dobonglife.domain.promotion.infrastructure.adapter;

import com.umust.dobonglife.domain.promotion.application.port.out.LoadPresetPort;
import com.umust.dobonglife.domain.promotion.domain.entity.Preset;
import com.umust.dobonglife.domain.promotion.infrastructure.jpa.PresetJpaRepository;
import com.umust.dobonglife.global.common.constant.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PresetPersistenceAdapter implements LoadPresetPort {

    private final PresetJpaRepository presetJpaRepository;

    @Override
    public Optional<Preset> findFirstByCategory(Category category) {
        return presetJpaRepository.findFirstByCategory(category);
    }
}
