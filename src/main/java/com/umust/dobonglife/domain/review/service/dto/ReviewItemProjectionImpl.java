package com.umust.dobonglife.domain.review.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReviewItemProjectionImpl implements ReviewItemProjection {
    private Long reviewId;
    private String placeName;
    private String courseName;
    private Double rating;
    private String contentSummary;
    private LocalDate writtenDate;
    private Integer likeCount;
    private Integer imageCount;
    private String thumbnailUrl;
}
