package com.umust.dobonglife.domain.place.domain.repository;

import com.umust.dobonglife.domain.place.domain.entity.PlaceLike;
import com.umust.dobonglife.domain.place.domain.repository.custom.PlaceLikeRepositoryCustom;
import com.umust.dobonglife.global.common.model.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long>, PlaceLikeRepositoryCustom {
    boolean existsByUserIdAndPlaceIdAndStatus(Long userId, Long placeId, BaseStatus status);
    long countByUserId(Long userId);
}
