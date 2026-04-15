package com.umust.dobonglife.domain.review.infrastructure.adapter;

import com.umust.dobonglife.domain.review.application.dto.MyReviewResponse;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.application.port.out.LoadReviewPort;
import com.umust.dobonglife.domain.review.application.port.out.SaveReviewPort;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.infrastructure.jpa.ReviewRepository;
import com.umust.dobonglife.global.common.constant.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewPersistenceAdapter implements LoadReviewPort, SaveReviewPort {

    private final ReviewRepository reviewJpaRepository;

    // ── LoadReviewPort ──

    @Override
    public Optional<Review> findById(Long reviewId) {
        return reviewJpaRepository.findById(reviewId);
    }

    @Override
    public List<ReviewSummaryResponse> findReviews(TargetType targetType, Long targetId, Long lastId, int size) {
        return reviewJpaRepository.findReviews(targetType, targetId, lastId, size);
    }

    @Override
    public List<MyReviewResponse> findMyReviews(Long userId, TargetType targetType, Long lastId, int size) {
        return reviewJpaRepository.findMyReviews(userId, targetType, lastId, size);
    }

    // ── SaveReviewPort ──

    @Override
    public Review save(Review review) {
        return reviewJpaRepository.save(review);
    }
}
