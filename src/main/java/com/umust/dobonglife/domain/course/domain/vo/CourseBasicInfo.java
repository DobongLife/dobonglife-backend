package com.umust.dobonglife.domain.course.domain.vo;

import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 코스 기본 정보
 * 제목, 부제목, 난이도, 소요시간, 테마, 태그
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseBasicInfo {

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String subTitle;

    @Column(nullable = false)
    private Long duration;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseLevel level;

    // tag

    public CourseBasicInfo(String title, String subTitle, Long duration, CourseLevel level) {
        this.title = title;
        this.subTitle = subTitle;
        this.duration = duration;
        this.level = level;
    }
}
