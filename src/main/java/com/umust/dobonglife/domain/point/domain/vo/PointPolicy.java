package com.umust.dobonglife.domain.point.domain.vo;

import lombok.Getter;

@Getter
public enum PointPolicy {

    REVIEW_CREATE("리뷰 등록", 10L);

    private final String title;
    private final long point;

    PointPolicy(String title, long point) {
        this.title = title;
        this.point = point;
    }
}

