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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "thumbnail_id")
    private CourseImage thumbnail;

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
}
