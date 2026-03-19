package com.umust.dobonglife.domain.place.domain.repository;

import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.global.common.model.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByIdAndStatus(Long id, BaseStatus status);

    List<Place> findAllByIdIn(List<Long> ids);

    List<Place> findAllByStatus(BaseStatus status);

    @Modifying
    @Query("""
        UPDATE Place p SET
            p.reviewCount = p.reviewCount + 1,
            p.ratingSum = p.ratingSum + :rating,
            p.averageRating = ROUND((p.ratingSum + :rating) / (p.reviewCount + 1), 1)
        WHERE p.id = :placeId
    """)
    void addReview(@Param("placeId") Long placeId, @Param("rating") Double rating);

    @Modifying
    @Query("""
        UPDATE Place p SET
            p.reviewCount = CASE WHEN p.reviewCount > 0 THEN p.reviewCount - 1 ELSE 0 END,
            p.ratingSum = CASE WHEN p.ratingSum >= :rating THEN p.ratingSum - :rating ELSE 0 END,
            p.averageRating = CASE WHEN p.reviewCount > 1
                THEN ROUND((p.ratingSum - :rating) / (p.reviewCount - 1), 1)
                ELSE 0.0 END
        WHERE p.id = :placeId
    """)
    void removeReview(@Param("placeId") Long placeId, @Param("rating") Double rating);
}
