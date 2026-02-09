package com.umust.dobonglife.domain.place.domain.repository;

import com.umust.dobonglife.domain.place.domain.entity.PlaceLike;
import com.umust.dobonglife.domain.place.domain.repository.custom.PlaceLikeRepositoryCustom;
import com.umust.dobonglife.global.common.model.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long>, PlaceLikeRepositoryCustom {
    boolean existsByUserIdAndPlaceIdAndStatus(Long userId, Long placeId, BaseStatus status);
    long countByUserId(Long userId);

    @Query("SELECT pl.place.id FROM PlaceLike pl " +
            "WHERE pl.user.id = :userId " +
            "AND pl.place.id IN :placeIds")
    Set<Long> findLikedPlaceIdsByUserIdAndPlaceIds(
            @Param("userId") Long userId,
            @Param("placeIds") List<Long> placeIds
    );
}
