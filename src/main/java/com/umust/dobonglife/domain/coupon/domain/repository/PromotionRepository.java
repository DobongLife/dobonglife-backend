package com.umust.dobonglife.domain.coupon.domain.repository;

import com.umust.dobonglife.domain.coupon.domain.entity.Promotion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    @Query("SELECT p FROM Promotion p " +
            "JOIN FETCH p.place " +
            "WHERE (:lastId IS NULL OR p.id < :lastId) " +
            "ORDER BY p.id DESC")
    Slice<Promotion> findPromotionWithPlaceNoOffset(Long lastId, Pageable pageable);


}
