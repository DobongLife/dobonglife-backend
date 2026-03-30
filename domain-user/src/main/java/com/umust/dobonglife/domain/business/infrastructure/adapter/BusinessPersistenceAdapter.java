package com.umust.dobonglife.domain.business.infrastructure.adapter;

import com.umust.dobonglife.domain.business.application.port.out.LoadBusinessPort;
import com.umust.dobonglife.domain.business.infrastructure.jpa.BusinessJpaRepository;
import com.umust.dobonglife.global.common.constant.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BusinessPersistenceAdapter implements LoadBusinessPort {

    private final BusinessJpaRepository businessJpaRepository;

    @Override
    public boolean existsByUserId(Long userId) {
        return businessJpaRepository.existsByUserId(userId);
    }

    @Override
    public Optional<Category> findCategoryByUserId(Long userId) {
        return businessJpaRepository.findCategoryByUserId(userId);
    }
}
