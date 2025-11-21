package com.umust.dobonglife.domain.course.domain.entity;

import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.regex.Pattern;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = true)
    private String subTitle;
    @Column(nullable = false)
    @ElementCollection
    private List<CourseTheme> themes;
    @Column(nullable = false)
    private Double duration;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseLevel level;

    @Column(nullable = true)
    @ElementCollection
    @CollectionTable(name = "course_tags", joinColumns = @JoinColumn(name = "course_id"))
    private List<String> tags;

    @Column(nullable = false)
    @ElementCollection
    @CollectionTable(name = "course_images", joinColumns = @JoinColumn(name = "course_id"))
    private List<String> imageUrls;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(nullable = false)
    private String meetingPlace;
    @Column(nullable = false)
    private String contact;
    @Column(nullable = true)
    private String cost;
    @Column(nullable = false)
    private Integer maxNum;
    @Column(nullable = false)
    private String ageLimit;
    @Column(columnDefinition = "TEXT")
    private String cancelPolicy;
    @Column(columnDefinition = "TEXT")
    private String weatherPolicy;

    @Column(nullable = true)
    @ElementCollection // @Convert(converter = StringListConverter.class)
    @CollectionTable(name = "course_highLights", joinColumns = @JoinColumn(name = "course_id"))
    private List<String> highlights;

    @Column(nullable = true)
    @ElementCollection
    @CollectionTable(name = "course_exclusions", joinColumns = @JoinColumn(name = "course_id"))
    private List<String> exclusions;

    @Column(nullable = true)
    @ElementCollection
    @CollectionTable(name = "course_inclusions", joinColumns = @JoinColumn(name = "course_id"))
    private List<String> inclusions;

    public Course(String title, String subTitle, List<CourseTheme> themes, Double duration, CourseLevel level, List<String> tags, List<String> imageUrls, String content, String meetingPlace, String contact, String cost, Integer maxNum, String ageLimit, String cancelPolicy, String weatherPolicy, List<String> highlights, List<String> exclusions, List<String> inclusions) {
        this.title = title;
        this.subTitle = subTitle;
        this.themes = themes;
        this.duration = duration;
        this.level = level;
        this.tags = tags;
        this.imageUrls = imageUrls;
        this.content = content;
        this.meetingPlace = meetingPlace;
        this.contact = contact;
        this.cost = cost;
        this.maxNum = maxNum;
        this.ageLimit = ageLimit;
        this.cancelPolicy = cancelPolicy;
        this.weatherPolicy = weatherPolicy;
        this.highlights = highlights;
        this.exclusions = exclusions;
        this.inclusions = inclusions;
    }

    public static Course create(String title, String subTitle, List<CourseTheme> themes, Double duration, CourseLevel level, List<String> tags, List<String> imageUrls, String content, String meetingPlace, String contact, String cost, int maxNum, String ageLimit, String cancelPolicy, String weatherPolicy, List<String> highlights, List<String> exclusions, List<String> inclusions) {
        validateCost(cost);
        validateAgeLimit(ageLimit);

        return new Course(title, subTitle, themes, duration, level, tags, imageUrls, content, meetingPlace, contact, cost, maxNum, ageLimit, cancelPolicy, weatherPolicy, highlights, exclusions, inclusions);
    }

    // 검증 메서드

    /**
     * 비용 검증
     * - 선택 (무료 강좌 가능)
     * - 값이 있으면 "무료" 또는 숫자
     * - 숫자인 경우 0 이상
     */
    private static void validateCost(String cost) {
        if (cost == null || cost.trim().isEmpty()) {
            return; // null 허용 (무료로 간주)
        }

        // "무료"인 경우 허용
        if (cost.trim().equals("무료")) {
            return;
        }

        // 숫자인 경우 검증
        try {
            int costValue = Integer.parseInt(cost.replaceAll("[^0-9]", ""));
            if (costValue < 0) {
                throw new IllegalArgumentException("비용은 0원 이상이어야 합니다");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("비용은 '무료' 또는 숫자여야 합니다");
        }
    }

    /**
     * 나이 제한 검증
     * - 필수 값
     * - "제한없음" 또는 "만 X세 이상" 형식
     */
    private static void validateAgeLimit(String ageLimit) {
        if (ageLimit == null || ageLimit.trim().isEmpty()) {
            throw new IllegalArgumentException("나이 제한 정보는 필수입니다");
        }

        // "제한없음"인 경우 허용
        if (ageLimit.trim().equals("제한없음")) {
            return;
        }

        // "만 X세 이상" 형식 검증
        Pattern agePattern = Pattern.compile("^만 [0-9]{1,2}세 이상$");
        if (!agePattern.matcher(ageLimit).matches()) {
            throw new IllegalArgumentException("나이 제한은 '제한없음' 또는 '만 X세 이상' 형식이어야 합니다");
        }
    }
}
