package com.umust.dobonglife.domain.promotion.domain.repository;

import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @EntityGraph(attributePaths = {"images"})
    List<Promotion> findAllByIdIn(List<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Promotion p WHERE p.id = :id")
    Optional<Promotion> findByIdForUpdate(@Param("id") Long id);

    @Query("""
        SELECT p FROM Promotion p
        WHERE (:lastId IS NULL OR p.id < :lastId)
        ORDER BY p.id DESC
    """)
    Slice<Promotion> findPromotionNoOffset(@Param("lastId") Long lastId, Pageable pageable);

    @Query("""
        SELECT p FROM Promotion p
        WHERE p.priority IS NOT NULL
        AND (:lastId IS NULL OR p.id < :lastId)
        ORDER BY p.priority ASC, p.id DESC
    """)
    Slice<Promotion> findBannerNoOffset(@Param("lastId") Long lastId, Pageable pageable);
}
