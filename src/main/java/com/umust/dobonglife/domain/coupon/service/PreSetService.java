package com.umust.dobonglife.domain.coupon.service;

import com.umust.dobonglife.domain.coupon.controller.dto.response.PresetResponse;
import com.umust.dobonglife.domain.coupon.domain.repository.PresetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PreSetService {
    private final PresetRepository presetRepository;

    public List<PresetResponse> getAllPresets() {
        return presetRepository.findAll().stream()
                .map(PresetResponse::from)
                .toList();
    }
}
