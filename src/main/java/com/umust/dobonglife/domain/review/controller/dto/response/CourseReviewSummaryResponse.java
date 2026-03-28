package com.umust.dobonglife.domain.review.controller.dto.response;

import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.global.common.Identifiable;

import java.time.LocalDateTime;
import java.util.List;

public record CourseReviewSummaryResponse(Long reviewId,
                                    String name,
                                    Double rating,
                                    String content,
                                    List<String> imageUrls,
                                    LocalDateTime updatedAt,
                                    boolean owner,
                                    CourseSummaryResponse courseInfo
                                    ) implements Identifiable {
    public static CourseReviewSummaryResponse from(Long userId, Review review, CourseSummaryResponse courseInfo) {
        boolean owner = userId == review.getUser().getId(); // TODO: 위치 다시 고민
        return new CourseReviewSummaryResponse(
                review.getId(),
                review.getUser().getName(),
                review.getRating(),
                review.getContent(),
                review.getImageUrls(),
                review.getUpdatedAt(),
                owner,
                courseInfo
        );
    }

    @Override
    public Long getId() {
        return reviewId;
    }
}
