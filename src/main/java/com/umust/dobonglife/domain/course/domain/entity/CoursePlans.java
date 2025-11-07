package com.umust.dobonglife.domain.course.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String content;

    private CoursePlans(Long courseId, LocalDateTime dateTime, String title, String content) {
        this.courseId = courseId;
        this.dateTime = dateTime;
        this.title = title;
        this.content = content;
    }

    public static CoursePlans create(Long courseId, LocalDateTime dateTime, String title, String content) {
        return new CoursePlans(courseId, dateTime, title, content);
    }
}

