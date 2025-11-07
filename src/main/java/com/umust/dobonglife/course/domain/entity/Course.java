package com.umust.dobonglife.course.domain.entity;

import com.umust.dobonglife.course.domain.constant.CourseLevel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id", nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = true)
    private String subTitle;
    @Column(nullable = false)
    private Double duration;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseLevel level;

    @Column(nullable = true)
    @ElementCollection
    @CollectionTable(name = "course_tags", joinColumns = @JoinColumn(name = "course_id"))
    private List<String> tags;

    @Column(nullable = false)
    @ElementCollection
    @CollectionTable(name = "course_images", joinColumns = @JoinColumn(name = "course_id"))
    private List<String> imageUrls;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(nullable = false)
    private String meetingPlace;
    @Column(nullable = false)
    private String contact;
    @Column(nullable = true)
    private String cost;
    @Column(nullable = false)
    private Integer maxNum;
    @Column(nullable = false)
    private String ageLimit;
    @Column(columnDefinition = "TEXT")
    private String cancelPolicy;
    @Column(columnDefinition = "TEXT")
    private String weatherPolicy;

    @Column(nullable = true)
    @ElementCollection // @Convert(converter = StringListConverter.class)
    @CollectionTable(name = "course_highLights", joinColumns = @JoinColumn(name = "course_id"))
    private List<String> highlights;

    @Column(nullable = true)
    @ElementCollection
    @CollectionTable(name = "course_exclusions", joinColumns = @JoinColumn(name = "course_id"))
    private List<String> exclusions;

    @Column(nullable = true)
    @ElementCollection
    @CollectionTable(name = "course_inclusions", joinColumns = @JoinColumn(name = "course_id"))
    private List<String> inclusions;
}
