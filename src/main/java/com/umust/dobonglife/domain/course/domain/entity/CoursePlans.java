package com.umust.dobonglife.domain.course.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoursePlans {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exclusions_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(nullable = true)
    private Long placeId;

    @Column(nullable = true)
    private Long isOrder;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String content;

    @Builder
    public CoursePlans(Course course, Long placeId, Long isOrder, String title, String content) {
        this.course = course;
        this.placeId = placeId;
        this.isOrder = isOrder;
        this.title = title;
        this.content = content;
    }

    public void assignCourse(Course course) {
        this.course = course;
    }
}

