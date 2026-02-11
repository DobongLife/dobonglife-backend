package com.umust.dobonglife.domain.review.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
        User user = review.getUser();
        boolean owner = (user != null && userId != null) && userId.equals(user.getId());
        return new ReviewSummaryResponse(
                review.getId(),
                (user != null) ? user.getName() : "알 수 없는 사용자",
                review.getRating(),
                review.getContent(),
                review.getImageUrls(),
                review.getUpdatedAt(),
                owner
        );
    }

    @JsonIgnore
    @Override
    public Long getId() {
        return reviewId;
    }
}

