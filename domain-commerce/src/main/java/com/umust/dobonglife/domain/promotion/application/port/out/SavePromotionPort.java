package com.umust.dobonglife.domain.promotion.application.port.out;

import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;

public interface SavePromotionPort {

    Promotion save(Promotion promotion);
}
