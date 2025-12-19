package com.umust.dobonglife.domain.reviewLike.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id", nullable = false)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "review_id")
    private Long reviewId;

    @Builder
    public ReviewLike(Long userId, Long reviewId) {
        this.userId = userId;
        this.reviewId = reviewId;
    }
}
