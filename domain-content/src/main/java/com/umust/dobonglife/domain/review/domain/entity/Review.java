package com.umust.dobonglife.domain.review.domain.entity;

import com.umust.dobonglife.domain.review.domain.vo.ReviewStatus;
import com.umust.dobonglife.domain.review.exception.ReviewErrorCode;
import com.umust.dobonglife.domain.review.exception.ReviewException;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    private Long userId;
    private Long targetId;

    @Enumerated(EnumType.STRING)
    private TargetType targetType;
    @Column(columnDefinition = "DECIMAL(2,1)")
    private Double rating;
    @Column(length = 255)
    private String content;
    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "thumbnail_id")
    private ReviewImage thumbnail;

    @BatchSize(size = 50)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "review_id", nullable = false)
    @OrderBy("sortOrder ASC")
    private List<ReviewImage> images = new ArrayList<>();

    @Builder
    private Review(Long userId, Long targetId, TargetType targetType, Double rating, String content) {
        this.userId = userId;
        this.targetId = targetId;
        this.targetType = targetType;
        this.rating = rating;
        this.content = content;
        this.reviewStatus = ReviewStatus.POSTED;
    }

    public void validateOwner(Long userId) {
        if (!this.userId.equals(userId)) {
            throw new ReviewException(ReviewErrorCode.NOT_REVIEW_OWNER);
        }
    }

    public void attachImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) return;
        List<ReviewImage> reviewImages = ReviewImage.ofUrls(imageUrls);
        this.thumbnail = reviewImages.get(0);
        this.images.addAll(reviewImages);
    }

    public void delete() {
        this.reviewStatus = ReviewStatus.DELETED;
    }
}
