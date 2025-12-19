package com.umust.dobonglife.domain.reviewLike.service;

import com.umust.dobonglife.domain.reviewLike.controller.dto.request.ReviewLikeResponse;
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

    public boolean isReviewFavorite(Long userId, Long courseId) {
        return reviewLikeRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    @Transactional
    public ReviewLikeResponse updateReviewLike(Long courseId, Long userId) {
        ReviewLike reviewLike = reviewLikeRepository.findByCourseIdAndUserId(courseId, userId)
                .orElseThrow(() -> new EntityExistsException("코스 좋아요 엔티티를 찾을 수 없습니다."));

        if(reviewLike != null){
            reviewLikeRepository.delete(reviewLike);
            return ReviewLikeResponse.from(userId, courseId, null, false);
        }

        ReviewLike newCourseLike = new ReviewLike(userId, courseId,null);
        reviewLikeRepository.save(newCourseLike);
        return ReviewLikeResponse.from(userId, courseId, null, true);
    }
}
