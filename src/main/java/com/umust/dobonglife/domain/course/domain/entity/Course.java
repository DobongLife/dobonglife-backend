package com.umust.dobonglife.domain.course.domain.entity;

import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.vo.CourseBasicInfo;
import com.umust.dobonglife.domain.course.domain.vo.CourseOperationInfo;
import com.umust.dobonglife.domain.course.domain.vo.CoursePolicyInfo;
import com.umust.dobonglife.domain.course.domain.vo.CourseReviewStats;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    // 기본 정보
    @Embedded
    private CourseBasicInfo basicInfo;

    // 운영 정보
    @Embedded
    private CourseOperationInfo operationInfo;

    // 정책 정보
    @Embedded
    private CoursePolicyInfo policyInfo;

    // 리뷰 통계
    @Embedded
    private CourseReviewStats reviewStats;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_themes", joinColumns = @JoinColumn(name = "course_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "theme")
    private List<CourseTheme> themes = new ArrayList<>();

    // 태그 (선택, 자주 필요)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_tags", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    // 이미지 (필수, 목록에서도 필요)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "course_images", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    // 상세 설명 (선택, 상세 조회 시에만 필요함으로 LAZY 로딩)
    @OneToOne(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private CourseDescription description;

    private Course(CourseBasicInfo basicInfo,
                   CourseOperationInfo operationInfo,
                   CoursePolicyInfo policyInfo,
                   List<CourseTheme> themes,
                   List<String> tags,
                   List<String> imageUrls) {
        this.basicInfo = basicInfo;
        this.operationInfo = operationInfo;
        this.policyInfo = policyInfo;
        this.reviewStats = new CourseReviewStats(0.0, 0L);
        this.themes = themes != null ? themes : new ArrayList<>();
        this.tags = tags != null ? tags : new ArrayList<>();
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }

    public static Course create(
            String title, String subTitle, Double duration, String level,
            String meetingPlace, String contact, String cost, int maxNum, String ageLimit,
            String cancelPolicy, String weatherPolicy,
            List<CourseTheme> themes, List<String> tags, List<String> imageUrls,
            String content, List<String> highlights, List<String> exclusions, List<String> inclusions) {

        CourseBasicInfo basicInfo = new CourseBasicInfo(
                title, subTitle, duration, CourseLevel.valueOf(level)
        );

        CourseOperationInfo operationInfo = new CourseOperationInfo(
                meetingPlace, contact, cost, maxNum, ageLimit
        );

        CoursePolicyInfo policyInfo = new CoursePolicyInfo(
                cancelPolicy, weatherPolicy
        );

        Course course = new Course(basicInfo, operationInfo, policyInfo, themes, tags, imageUrls);

        // 상세 설명 설정
        CourseDescription description = new CourseDescription(
                course, content, highlights, exclusions, inclusions
        );
        course.setDescription(description);

        return course;
    }

    private void setDescription(CourseDescription description) {
        this.description = description;
    }

    public void updateRatingInfo(Double newAverageRating, Long newReviewCount) {
        this.reviewStats.update(newAverageRating, newReviewCount);
    }

    // TODO: Getter 편의 메서드, 불필요시 삭제
    public String getTitle() {
        return basicInfo.getTitle();
    }

    public CourseLevel getLevel() {
        return basicInfo.getLevel();
    }

    public Double getAverageRating() {
        return reviewStats.getAverageRating();
    }

    public Long getReviewCount() {
        return reviewStats.getReviewCount();
    }
}
