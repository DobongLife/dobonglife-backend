package com.umust.dobonglife.domain.course.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course_plans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoursePlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_plan_id")
    private Long id;

    private Long placeId;

    private Short sortOrder;

    @Column(length = 50)
    private String title;
    @Column(length = 100)
    private String content;
}
