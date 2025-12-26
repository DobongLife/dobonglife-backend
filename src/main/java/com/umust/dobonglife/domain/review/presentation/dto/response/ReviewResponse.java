package com.umust.dobonglife.domain.review.presentation.dto.response;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseRegisterResponse;
import com.umust.dobonglife.domain.review.domain.entity.Review;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReviewResponse(Long reviewId,
                             String content) {

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getContent()
        );
    }
}
