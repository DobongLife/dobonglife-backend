package com.umust.dobonglife.course.domain.entity;

import com.umust.dobonglife.course.domain.constant.CourseLevel;
import com.umust.dobonglife.course.domain.constant.CourseTheme;
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
    @ElementCollection
    private List<CourseTheme> themes;
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

    public Course(String title, String subTitle, List<CourseTheme> themes, Double duration, CourseLevel level, List<String> tags, List<String> imageUrls, String content, String meetingPlace, String contact, String cost, Integer maxNum, String ageLimit, String cancelPolicy, String weatherPolicy, List<String> highlights, List<String> exclusions, List<String> inclusions) {
        this.title = title;
        this.subTitle = subTitle;
        this.themes = themes;
        this.duration = duration;
        this.level = level;
        this.tags = tags;
        this.imageUrls = imageUrls;
        this.content = content;
        this.meetingPlace = meetingPlace;
        this.contact = contact;
        this.cost = cost;
        this.maxNum = maxNum;
        this.ageLimit = ageLimit;
        this.cancelPolicy = cancelPolicy;
        this.weatherPolicy = weatherPolicy;
        this.highlights = highlights;
        this.exclusions = exclusions;
        this.inclusions = inclusions;
    }

    public static Course create(String title, String subTitle, List<CourseTheme> themes, Double duration, CourseLevel level, List<String> tags, List<String> imageUrls, String content, String meetingPlace, String contact, String cost, int maxNum, String ageLimit, String cancelPolicy, String weatherPolicy, List<String> highlights, List<String> exclusions, List<String> inclusions) {
        validateTitle(title);
        validateDuration(duration);
        validateLevel(level);
        validateTag(tags);
        validateImageUrl(imageUrls);
        validateContent(content);
        validateMeetingPlace(meetingPlace);
        validateContact(contact);
        validateCost(cost);
        validateMaxNum(maxNum);
        validateAgeLimit(ageLimit);
        validateCancelPolicy(cancelPolicy);
        validateWeatherPolicy(weatherPolicy);
        validateHighlights(highlights);
        validateExclusions(exclusions);
        validateInclusions(inclusions);

        return new Course(title, subTitle, themes, duration, level, tags, imageUrls, content, meetingPlace, contact, cost, maxNum, ageLimit, cancelPolicy, weatherPolicy, highlights, exclusions, inclusions);
    }
}
