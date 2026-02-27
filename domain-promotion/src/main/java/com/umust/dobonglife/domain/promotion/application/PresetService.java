package com.umust.dobonglife.domain.promotion.application;

import com.umust.dobonglife.domain.promotion.domain.entity.Preset;
import com.umust.dobonglife.domain.promotion.domain.repository.PresetRepository;
import com.umust.dobonglife.domain.promotion.exception.PromotionErrorCode;
import com.umust.dobonglife.domain.promotion.exception.PromotionException;
import com.umust.dobonglife.global.common.constant.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PresetService {

    private final PresetRepository presetRepository;

    public Preset getPresetByCategory(Category category) {
        return presetRepository.findFirstByCategory(category)
                .orElseThrow(() -> new PromotionException(PromotionErrorCode.PRESET_NOT_FOUND));
    }
}
