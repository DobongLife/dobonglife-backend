package com.umust.dobonglife.domain.course.domain.entity;

import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.vo.CourseBasicInfo;
import com.umust.dobonglife.domain.course.domain.vo.CourseOperationInfo;
import com.umust.dobonglife.domain.course.domain.vo.CoursePolicyInfo;
import com.umust.dobonglife.domain.course.domain.vo.CourseReviewStats;
import com.umust.dobonglife.global.common.model.BaseEntity;
import com.umust.dobonglife.global.common.model.BaseStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id", nullable = false)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    // 기본 정보
    @Embedded
    private CourseBasicInfo basicInfo;

    // 리뷰 통계
    @Embedded
    private CourseReviewStats reviewStats;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "course_themes",
            joinColumns = @JoinColumn(name = "course_id")
    )
    @Column(name = "theme")
    @Enumerated(EnumType.STRING) // Enum 사용 시 필수
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

    @Builder
    public Course(Long userId, CourseBasicInfo basicInfo, CourseReviewStats reviewStats, List<CourseTheme> themes, List<String> tags, List<String> imageUrls, CourseDescription description) {
        validateCourse(basicInfo);
        this.userId = userId;
        this.basicInfo = basicInfo;

        this.reviewStats = (reviewStats != null) ? reviewStats : new CourseReviewStats(0.0, 0L);
        this.themes = themes != null ? themes : new ArrayList<>();
        this.tags = tags != null ? tags : new ArrayList<>();
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
        this.description = description;

        if (description != null) {
            description.assignCourse(this);
        }
    }

    private void validateCourse(CourseBasicInfo basicInfo) {
        if (basicInfo == null) throw new IllegalArgumentException("기본 정보는 필수입니다.");
    }

    // TODO: Getter 편의 메서드, 불필요시 삭제
    public String getTitle() {
        return basicInfo.getTitle();
    }

    public String getSubTitle() {
        return basicInfo.getSubTitle();
    }

    public CourseLevel getLevel() {
        return basicInfo.getLevel();
    }

    public Double getAverageRating() {
        return reviewStats.getAverageRating();
    }

    public Double getRatingSum() {
        return reviewStats.getRatingSum();
    }

    public Long getReviewCount() {
        return reviewStats.getReviewCount();
    }

    public void applyNewReview(Double newRating) {
        if (newRating == null) throw new IllegalArgumentException("rating is null");
        reviewStats.applyNewReview(newRating);
    }

    public void updateReviewRating(Double oldRating, Double newRating) {
        if (oldRating == null || newRating == null) throw new IllegalArgumentException("rating is null");
        if (getReviewCount() <= 0) {
            throw new IllegalStateException("Cannot update review rating when reviewCount is 0");
        }
        reviewStats.updateReview(oldRating, newRating);
    }

    public void deleteReview(Double rating) {
        if (rating == null) throw new IllegalArgumentException("rating is null");
        reviewStats.deleteReview(rating);
    }

    public void updateBasicInfo(CourseBasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }

    public void deactivate() {
        if (this.status == BaseStatus.INACTIVE) {
            return;
        }
        this.status = BaseStatus.INACTIVE;
    }
}
