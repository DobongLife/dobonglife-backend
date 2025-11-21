package com.umust.dobonglife.domain.review.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewRegisterRequest {

    private Long placeId;

    private Long courseId;

    private Double rating;

    private String title;

    private String content;

    private List<String> templates;
}
