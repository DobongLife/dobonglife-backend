package com.umust.dobonglife.domain.course.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 코스 리뷰 통계
 * 평균 평점, 리뷰 개수
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseReviewStats {

    @Column(nullable = false)
    private Double averageRating = 0.0;

    @Column(nullable = false)
    private Long reviewCount = 0L;

    public CourseReviewStats(Double averageRating, Long reviewCount) {
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    public void update(Double newAverageRating, Long newReviewCount) {
        this.averageRating = newAverageRating;
        this.reviewCount = newReviewCount;
    }
}
