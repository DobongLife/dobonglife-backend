package com.umust.dobonglife.domain.coupon.service;

import com.umust.dobonglife.domain.coupon.domain.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;
}
