package com.umust.dobonglife.domain.promotion.infrastructure.adapter;

import com.umust.dobonglife.domain.promotion.application.port.out.LoadPromotionPort;
import com.umust.dobonglife.domain.promotion.application.port.out.SavePromotionPort;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.domain.promotion.infrastructure.jpa.PromotionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PromotionPersistenceAdapter implements LoadPromotionPort, SavePromotionPort {

    private final PromotionJpaRepository promotionJpaRepository;

    @Override
    public Optional<Promotion> findById(Long promotionId) {
        return promotionJpaRepository.findById(promotionId);
    }

    @Override
    public Optional<Promotion> findByIdForUpdate(Long promotionId) {
        return promotionJpaRepository.findByIdForUpdate(promotionId);
    }

    @Override
    public List<Promotion> findAllByIdIn(List<Long> ids) {
        return promotionJpaRepository.findAllByIdIn(ids);
    }

    @Override
    public Slice<Promotion> findPromotionNoOffset(Long lastId, Pageable pageable) {
        return promotionJpaRepository.findPromotionNoOffset(lastId, pageable);
    }

    @Override
    public Slice<Promotion> findBannerNoOffset(Long lastId, Pageable pageable) {
        return promotionJpaRepository.findBannerNoOffset(lastId, pageable);
    }

    @Override
    public Slice<Promotion> findByBusinessIdNoOffset(Long businessId, Long lastId, Pageable pageable) {
        return promotionJpaRepository.findByBusinessIdNoOffset(businessId, lastId, pageable);
    }

    @Override
    public Promotion save(Promotion promotion) {
        return promotionJpaRepository.save(promotion);
    }
}
