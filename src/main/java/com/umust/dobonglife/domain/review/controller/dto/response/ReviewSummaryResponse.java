package com.umust.dobonglife.domain.review.controller.dto.response;

import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.Identifiable;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewSummaryResponse(Long reviewId,
                                          String name,
                                          Double rating,
                                          String content,
                                          List<String> imageUrls,
                                          LocalDateTime updatedAt,
                                          boolean owner
) implements Identifiable {
    public static ReviewSummaryResponse from(Long userId, Review review) {
        if (review == null) return null;

        User user = review.getUser();
        String userName = "알 수 없는 사용자";

        boolean isOwner = false;

        if (user != null) {
            try {
                userName = user.getName();
                if (userId != null && userId.equals(user.getId())) {
                    isOwner = true;
                }
            } catch (Exception e) {
                userName = "알 수 없는 사용자";
            }
        }

        return new ReviewSummaryResponse(
                review.getId(),
                userName,
                review.getRating(),
                review.getContent(),
                review.getImageUrls(),
                review.getUpdatedAt(),
                isOwner
        );
    }

    @Override
    public Long getId() {
        return reviewId;
    }
}

