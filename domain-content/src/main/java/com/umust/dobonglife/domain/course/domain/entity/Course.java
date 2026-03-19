package com.umust.dobonglife.domain.course.domain.entity;

import com.umust.dobonglife.domain.course.domain.vo.Level;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "courses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @Column(length = 50)
    private String title;
    @Column(length = 100, nullable = true)
    private String subTitle;
    @Enumerated(EnumType.STRING)
    private Level level;
    private Long duration;

    @Column(columnDefinition = "DECIMAL(2,1)")
    private Double averageRating;
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double ratingSum;
    private Long reviewCount;

    private String thumbnailUrl;

    @BatchSize(size = 50)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "course_id", nullable = false)
    @OrderBy("sortOrder ASC")
    private List<CourseImage> images = new ArrayList<>();

    @BatchSize(size = 50)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "course_id", nullable = false)
    private List<CourseTheme> themes = new ArrayList<>();

    @BatchSize(size = 50)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "course_id", nullable = false)
    @OrderBy("sortOrder ASC")
    private List<CoursePlan> plans = new ArrayList<>();

    @BatchSize(size = 50)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "course_id", nullable = false)
    private List<CourseTag> tags = new ArrayList<>();

    @Column(length = 500)
    private String content;

    private Long userId;

    public static Course create(Long userId, String title, String subTitle, Level level, Long duration,
                                String content, List<String> imageUrls, List<CourseTheme> themes,
                                List<CoursePlan> plans, List<CourseTag> tags) {
        Course course = new Course();
        course.userId = userId;
        course.title = title;
        course.subTitle = subTitle;
        course.level = level;
        course.duration = duration;
        course.content = content;
        course.averageRating = 0.0;
        course.ratingSum = 0.0;
        course.reviewCount = 0L;

        List<CourseImage> courseImages = CourseImage.ofUrls(imageUrls);
        if (!imageUrls.isEmpty()) {
            course.thumbnailUrl = imageUrls.get(0);
        }
        course.images.addAll(courseImages);
        course.themes.addAll(themes);
        course.plans.addAll(plans);
        course.tags.addAll(tags);

        return course;
    }

    public void update(String title, String subTitle, Level level, Long duration,
                       String content, List<String> newImageUrls, List<String> deleteImageUrls,
                       List<CourseTheme> newThemes, List<CoursePlan> newPlans, List<CourseTag> newTags) {
        this.title = title;
        this.subTitle = subTitle;
        this.level = level;
        this.duration = duration;
        this.content = content;

        if (deleteImageUrls != null && !deleteImageUrls.isEmpty()) {
            this.images.removeIf(img -> deleteImageUrls.contains(img.getImageUrl()));
        }
        if (newImageUrls != null && !newImageUrls.isEmpty()) {
            short nextOrder = (short) this.images.size();
            for (String url : newImageUrls) {
                this.images.add(CourseImage.builder().imageUrl(url).sortOrder(nextOrder++).build());
            }
        }
        this.thumbnailUrl = this.images.isEmpty() ? null : this.images.get(0).getImageUrl();

        this.themes.clear();
        this.themes.addAll(newThemes);

        this.plans.clear();
        this.plans.addAll(newPlans);

        this.tags.clear();
        this.tags.addAll(newTags);
    }

    public boolean isOwner(Long userId) {
        return this.userId.equals(userId);
    }
}
