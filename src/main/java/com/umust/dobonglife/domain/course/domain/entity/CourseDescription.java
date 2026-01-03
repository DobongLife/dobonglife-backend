package com.umust.dobonglife.domain.course.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 코스 상세 설명 (별도 테이블)
 * content, highlights, exclusions, inclusions
 */
@Entity
@Table(name = "course_description")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseDescription {

    @Id
    private Long courseDescriptionId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    @CollectionTable(name = "course_highlights", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "highlight")
    private List<String> highlights = new ArrayList<>();

    public CourseDescription(Course course, String content,
                             List<String> highlights) {
        this.course = course;
        this.content = content;
        this.highlights = highlights != null ? highlights : new ArrayList<>();
    }

    public void assignCourse(Course course) {
        this.course = course;
    }

    public void update(String content, List<String> highlights) {
        this.content = content;
        this.highlights = highlights != null ? highlights : new ArrayList<>();
    }
}
