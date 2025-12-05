package com.umust.dobonglife.domain.review.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_like")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_like_id", nullable = false)
    private Long id;

    @JoinColumn(name = "course_id", nullable = false)
    private Long courseId;

    @JoinColumn(name = "user_id", nullable = false)
    private Long userId;

    private ReviewLike(Long courseId, Long userId) {
        this.courseId = courseId;
        this.userId = userId;
    }

    public static ReviewLike create(Long courseId, Long userId) {
        return new ReviewLike(courseId, userId);
    }
}
