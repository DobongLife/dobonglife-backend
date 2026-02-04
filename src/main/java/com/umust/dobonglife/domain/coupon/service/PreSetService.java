package com.umust.dobonglife.domain.coupon.service;

import com.umust.dobonglife.domain.business.service.BusinessService;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PresetResponse;
import com.umust.dobonglife.domain.coupon.domain.repository.PresetRepository;
import com.umust.dobonglife.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PreSetService {
    private final PresetRepository presetRepository;
    private final BusinessService businessService;

    public PresetResponse getPreset(Long userId) {
        String category = businessService.getCategoryUserId(userId);
        return presetRepository.findByCategory(category).stream()
                .findFirst() // 첫 번째 요소만
                .map(PresetResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("해당 카테고리의 프리셋이 없습니다."));
}
