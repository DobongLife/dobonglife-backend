package com.umust.dobonglife.domain.place.domain.entity;

import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.place.domain.constant.Theme;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CoursePlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_place_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course content;

    @Column
    @Enumerated(EnumType.STRING)
    private Theme theme;
}
