package com.umust.dobonglife.domain.promotion.application.port.out;

import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface LoadPromotionPort {

    Optional<Promotion> findById(Long promotionId);

    Optional<Promotion> findByIdForUpdate(Long promotionId);

    List<Promotion> findAllByIdIn(List<Long> ids);

    Slice<Promotion> findPromotionNoOffset(Long lastId, Pageable pageable);

    Slice<Promotion> findBannerNoOffset(Long lastId, Pageable pageable);

    Slice<Promotion> findByBusinessIdNoOffset(Long businessId, Long lastId, Pageable pageable);
}
