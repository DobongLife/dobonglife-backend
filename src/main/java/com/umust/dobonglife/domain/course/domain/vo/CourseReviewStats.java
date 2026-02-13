package com.umust.dobonglife.domain.course.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseReviewStats {

    @Column(nullable = false)
    private Double ratingSum = 0.0;

    @Column(nullable = false)
    private Long reviewCount = 0L;

    @Column(nullable = false)
    private Double averageRating = 0.0;

    public CourseReviewStats(Double ratingSum, Long reviewCount) {
        this.ratingSum = safe(ratingSum);
        this.reviewCount = safe(reviewCount);
        recalcAverage();
    }

    public void applyNewReview(double rating) {
        ratingSum += rating;
        reviewCount += 1;
        recalcAverage();
    }

    public void updateReview(double oldRating, double newRating) {
        // reviewCount는 변하지 않음
        ratingSum = ratingSum - oldRating + newRating;
        recalcAverage();
    }

    public void deleteReview(double rating) {
        ratingSum = ratingSum - rating;
        reviewCount = Math.max(0L, reviewCount - 1);
        if (reviewCount == 0L) {
            ratingSum = 0.0; // 음수/잔여 오차 방지
        }
        recalcAverage();
    }

    private void recalcAverage() {
        if (reviewCount == 0L) {
            averageRating = 0.0;
        } else {
            averageRating = ratingSum / reviewCount;
        }
    }

    private static double safe(Double v) { return v == null ? 0.0 : v; }
    private static long safe(Long v) { return v == null ? 0L : v; }
}