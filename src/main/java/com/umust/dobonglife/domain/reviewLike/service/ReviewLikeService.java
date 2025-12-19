package com.umust.dobonglife.domain.reviewLike.service;

import com.umust.dobonglife.domain.reviewLike.controller.dto.response.ReviewLikeResponse;
import com.umust.dobonglife.domain.reviewLike.domain.entity.ReviewLike;
import com.umust.dobonglife.domain.reviewLike.domain.repository.ReviewLikeRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {
    private final ReviewLikeRepository reviewLikeRepository;

    public boolean isReviewFavorite(Long userId, Long reviewId) {
        return reviewLikeRepository.existsByUserIdAndReviewId(userId, reviewId);
    }

    @Transactional
    public ReviewLikeResponse updateReviewLike(Long reviewId, Long userId) {
        ReviewLike reviewLike = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new EntityExistsException("코스 좋아요 엔티티를 찾을 수 없습니다."));

        if(reviewLike != null){
            reviewLikeRepository.delete(reviewLike);
            return ReviewLikeResponse.from(userId, reviewId, false);
        }

        ReviewLike newCourseLike = new ReviewLike(userId, reviewId);
        reviewLikeRepository.save(newCourseLike);
        return ReviewLikeResponse.from(userId, reviewId, true);
    }
}
