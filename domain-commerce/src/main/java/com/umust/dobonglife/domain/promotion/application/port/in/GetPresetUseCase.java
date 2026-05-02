package com.umust.dobonglife.domain.promotion.application.port.in;

import com.umust.dobonglife.domain.promotion.domain.entity.Preset;
import com.umust.dobonglife.global.common.constant.Category;

public interface GetPresetUseCase {

    Preset getPresetByCategory(Category category);
}
