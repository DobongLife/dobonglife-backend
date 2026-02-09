package com.umust.dobonglife.domain.place.domain.entity;

import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.place.domain.constant.Amenity;

import com.umust.dobonglife.global.common.model.BaseEntity;
import com.umust.dobonglife.global.common.model.constant.Category;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "places")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "sub_name", nullable = false)
    private String subName;

    @Column(name = "content", nullable = false)
    private String content;

    @ElementCollection(targetClass = Amenity.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "amenities", joinColumns = @JoinColumn(name = "place_id"))
    @Enumerated(EnumType.STRING)
    private List<Amenity> amenities;

    @Column(name = "address", nullable = false)
    private String address;

    @ElementCollection
    @CollectionTable(name = "place_images", joinColumns = @JoinColumn(name = "place_id"))
    @Column(name = "image_url", columnDefinition = "TEXT")
    private List<String> imageUrls;

    @Column(name = "operating_hour", nullable = false)
    private String operatingHour;

    @Column(name = "contact", nullable = false)
    private String contact;

    @Builder.Default
    @Column(name = "average_rating", nullable = false)
    private Double averageRating = 0.0;

    @Builder.Default
    @Column(name = "review_count", nullable = false)
    private Long reviewCount = 0L;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "place_themes",
            joinColumns = @JoinColumn(name = "place_id")
    )
    @Column(name = "theme")
    @Enumerated(EnumType.STRING)
    private List<CourseTheme> themes;

    @Column(name = "latitude", nullable = true)
    private Double latitude;

    @Column(name = "longitude", nullable = true)
    private Double longitude;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    public void applyNewReview(Double newRating) { // TODO: Course 처럼 분리할지 고민
        double totalScore = (this.averageRating * this.reviewCount) + newRating;
        this.reviewCount = this.reviewCount + 1;
        this.averageRating = totalScore / reviewCount;
    }

    public void applyDeleteReview(Double deletedRating) {
        double totalScore = (this.averageRating * this.reviewCount) - deletedRating;

        long newCount = this.reviewCount - 1;

        // 만약 0개가 될 경우
        if (newCount <= 0) {
            this.reviewCount = 0L;
            this.averageRating = 0.0;
            return;
        }

        this.reviewCount = newCount;
        this.averageRating = totalScore / newCount;

        if (!Double.isFinite(this.averageRating)) {
            this.averageRating = 0.0;
        }
    }

    public static List<String> amenityToStrings(Place place) {
        if (place == null || place.getAmenities() == null) {
            return Collections.emptyList();
        }

        return place.getAmenities().stream()
                .map(Amenity::getLabel)
                .toList();
    }

    public void changeImages(List<String> newImages) {
        if (newImages == null || newImages.isEmpty()) {
            throw new BusinessException(ErrorCode.PLACE_IMAGE_REQUIRED);
        }

        this.imageUrls.clear();
        this.imageUrls.addAll(newImages);
        this.thumbnailUrl = newImages.getFirst();
    }
}

