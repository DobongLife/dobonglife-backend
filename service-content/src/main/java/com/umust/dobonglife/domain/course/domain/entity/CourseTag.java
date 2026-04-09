package com.umust.dobonglife.domain.course.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "course_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_tag_id")
    private Long id;

    private String tag;

    public static CourseTag of(String tag) {
        CourseTag courseTag = new CourseTag();
        courseTag.tag = tag;
        return courseTag;
    }
}