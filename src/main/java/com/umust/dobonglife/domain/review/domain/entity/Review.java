package com.umust.dobonglife.domain.review.domain.entity;

<<<<<<< Updated upstream
import com.umust.dobonglife.domain.review.domain.constant.ReviewStatus;
=======
import com.umust.dobonglife.domain.review.domain.constant.ReviewTemplate;
>>>>>>> Stashed changes
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

<<<<<<< Updated upstream
import java.time.LocalDateTime;
import java.util.ArrayList;
=======
>>>>>>> Stashed changes
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

    @JoinColumn(name = "course_id", nullable = false)
    private Long courseId;

    @JoinColumn(name = "place_id", nullable = false)
    private Long placeId;

<<<<<<< Updated upstream
    @JoinColumn(name = "user_id", nullable = false)
    private Long userId;

=======
>>>>>>> Stashed changes
    @Column(name = "rating", nullable = true)
    private Double rating;

    @Column(name = "title", nullable = true)
    private String title;

    @Column(name = "content", nullable = true, columnDefinition = "VARCHAR(500)")
    private String content;

<<<<<<< Updated upstream

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReviewStatus status = ReviewStatus.POSTED;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    private List<String> imageUrls = new ArrayList<>();


    private Review(Long courseId, Long userId, Long placeId, Double rating, String title, String content, List<String> imageUrls) {
        this.courseId = courseId;
        this.placeId = placeId;
        this.userId = userId;
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.imageUrls = imageUrls;
    }

    public static Review create(Long courseId, Long userId, Long placeId, Double rating,
                                String title, String content, List<String> imageUrls) {
        validateRating(rating);
        return new Review(courseId, placeId, userId, rating, title, content, imageUrls);
=======
    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    private List<String> imageUrls;


    private Review(Long courseId, Long placeId, Double rating, String title, String content, List<String> imageUrls) {
        this.courseId = courseId;
        this.placeId = placeId;
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.imageUrls = imageUrls;
    }

    public static Review create(Long courseId, Long placeId, Double rating,
                                String title, String content, List<String> imageUrls) {
        validateRating(rating);
        return new Review(courseId, placeId, rating, title, content, imageUrls);
>>>>>>> Stashed changes
    }

    /**
     * 평점 검증
     * - Null 허용 (nullable = true)
     * - 값이 있으면 0.0 이상 5.0 이하
     */
    private static void validateRating(Double rating) {
        if (rating == null) {
            return;
        }
        if (rating < 0.0 || rating > 5.0) {
            throw new IllegalArgumentException("평점은 0.0 이상 5.0 이하여야 합니다.");
        }
    }
}
