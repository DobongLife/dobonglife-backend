package com.umust.dobonglife.domain.review.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewRegisterRequest {

    private Double rating;

    String title;

    String content;

    List<String> templates;








}
