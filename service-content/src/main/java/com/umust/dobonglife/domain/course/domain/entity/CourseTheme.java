package com.umust.dobonglife.domain.course.domain.entity;

import com.umust.dobonglife.domain.place.domain.vo.Theme;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "course_themes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseTheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_theme_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Theme theme;

    public static CourseTheme of(Theme theme) {
        CourseTheme courseTheme = new CourseTheme();
        courseTheme.theme = theme;
        return courseTheme;
    }
}