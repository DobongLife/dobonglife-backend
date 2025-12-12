package com.umust.dobonglife.domain.coupon.presentation.dto.response;

import java.util.List;

public record PromotionResponse (Long point,
                               List<PromotionItem> promotionList){
}
