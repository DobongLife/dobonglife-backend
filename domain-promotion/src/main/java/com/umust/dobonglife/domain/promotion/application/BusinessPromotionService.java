package com.umust.dobonglife.domain.promotion.application;

import com.umust.dobonglife.domain.promotion.domain.repository.PromotionRepository;
import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionRegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessPromotionService {

    private final PromotionRepository promotionRepository;
    public PromotionRegisterResponse registerCoupon(PromotionRegisterRequest request, Long userId, List<MultipartFile> imageFiles) {
    }
}
