package com.umust.dobonglife.domain.promotion.application.port.out;

import com.umust.dobonglife.domain.promotion.domain.entity.Preset;
import com.umust.dobonglife.global.common.constant.Category;

import java.util.Optional;

public interface LoadPresetPort {

    Optional<Preset> findFirstByCategory(Category category);
}
