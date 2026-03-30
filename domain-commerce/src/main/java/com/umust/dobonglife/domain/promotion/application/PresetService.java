package com.umust.dobonglife.domain.promotion.application;

import com.umust.dobonglife.domain.promotion.application.port.in.GetPresetUseCase;
import com.umust.dobonglife.domain.promotion.application.port.out.LoadPresetPort;
import com.umust.dobonglife.domain.promotion.domain.entity.Preset;
import com.umust.dobonglife.domain.promotion.exception.PromotionErrorCode;
import com.umust.dobonglife.domain.promotion.exception.PromotionException;
import com.umust.dobonglife.global.common.constant.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PresetService implements GetPresetUseCase {

    private final LoadPresetPort loadPresetPort;

    @Override
    public Preset getPresetByCategory(Category category) {
        return loadPresetPort.findFirstByCategory(category)
                .orElseThrow(() -> new PromotionException(PromotionErrorCode.PRESET_NOT_FOUND));
    }
}
