package com.umust.dobonglife.domain.course.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 코스 정책 정보
 * 취소 정책, 날씨 정책
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoursePolicyInfo {

    @Column(columnDefinition = "TEXT")
    private String cancelPolicy;

    @Column(columnDefinition = "TEXT")
    private String weatherPolicy;

    public CoursePolicyInfo(String cancelPolicy, String weatherPolicy) {
        this.cancelPolicy = cancelPolicy;
        this.weatherPolicy = weatherPolicy;
    }
}
