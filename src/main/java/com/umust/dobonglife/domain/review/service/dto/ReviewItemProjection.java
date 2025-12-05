package com.umust.dobonglife.domain.review.service.dto;

import java.time.LocalDate;

public interface ReviewItemProjection {
    Long getReviewId();
    String getPlaceName();
    String getCourseName();
    Double getRating();
    String getContentSummary();
    LocalDate getWrittenDate();
    Integer getLikeCount();
    Integer getImageCount();
    String getThumbnailUrl();
}
