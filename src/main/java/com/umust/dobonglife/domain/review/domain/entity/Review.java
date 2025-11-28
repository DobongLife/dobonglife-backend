package com.umust.dobonglife.domain.review.domain.entity;


import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.review.domain.constant.Template;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reviews")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = true)
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = true)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rating", nullable = false)
    private Double rating;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    @Column(columnDefinition = "TEXT")
    private List<String> reviewImages = new ArrayList<>();

    @ElementCollection(targetClass = Template.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "templates", joinColumns = @JoinColumn(name = "review_id"))
    @Enumerated(EnumType.STRING)
    private List<Template> templates = new ArrayList<>();
}
