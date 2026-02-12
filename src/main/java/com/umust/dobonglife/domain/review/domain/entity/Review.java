package com.umust.dobonglife.domain.review.domain.entity;
import com.umust.dobonglife.domain.review.domain.constant.ReviewStatus;
import com.umust.dobonglife.domain.review.controller.dto.request.CreateReviewRequest;
import com.umust.dobonglife.domain.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long id;

    @JoinColumn(name = "course_id", nullable = true)
    private Long courseId;

    @JoinColumn(name = "place_id", nullable = true)
    private Long placeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "rating", nullable = true)
    private Double rating;

    @Column(name = "content", nullable = true, columnDefinition = "VARCHAR(500)")
    private String content;

    @Column(name = "updated_at", nullable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReviewStatus status = ReviewStatus.POSTED;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    private List<String> imageUrls = new ArrayList<>();

    @Builder
    public Review(Long courseId, Long placeId, User user, Double rating, String content, List<String> imageUrls) {
        this.courseId = courseId;
        this.placeId = placeId;
        this.user = user;
        this.rating = rating;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
        this.status = ReviewStatus.POSTED;
        this.imageUrls = imageUrls;
    }

    private static void validateRating(Double rating) {
        if (rating == null) {
            return;
        }
        if (rating < 0.0 || rating > 5.0) {
            throw new IllegalArgumentException("평점은 0.0 이상 5.0 이하여야 합니다.");
        }
    }

    public void update(Long courseId, Long placeId, Double rating, String content, List<String> imageUrls) {
        this.courseId = courseId;
        this.placeId = placeId;
        this.rating = rating;
        this.content = content;
        this.imageUrls = imageUrls;
        this.updatedAt = LocalDateTime.now();
    }
}
