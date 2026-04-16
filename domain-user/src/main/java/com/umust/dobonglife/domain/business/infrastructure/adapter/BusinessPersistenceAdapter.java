package com.umust.dobonglife.domain.business.infrastructure.adapter;

import com.umust.dobonglife.domain.business.application.port.out.LoadBusinessPort;
import com.umust.dobonglife.domain.business.application.port.out.SaveBusinessPort;
import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.business.infrastructure.jpa.BusinessJpaRepository;
import com.umust.dobonglife.global.common.constant.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BusinessPersistenceAdapter implements LoadBusinessPort, SaveBusinessPort {

    private final BusinessJpaRepository businessJpaRepository;

    // ── LoadBusinessPort ──

    @Override
    public boolean existsByUserId(Long userId) {
        return businessJpaRepository.existsByUserId(userId);
    }

    @Override
    public Optional<Business> findByUserId(Long userId) {
        return businessJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<Category> findCategoryByUserId(Long userId) {
        return businessJpaRepository.findCategoryByUserId(userId);
    }

    // ── SaveBusinessPort ──

    @Override
    public Business save(Business business) {
        return businessJpaRepository.save(business);
    }

    @Override
    public void deleteById(Long id) {
        businessJpaRepository.deleteById(id);
    }

    @Override
    public void nullifyPlaceById(Long businessId) {
        businessJpaRepository.nullifyPlaceById(businessId);
    }
}
