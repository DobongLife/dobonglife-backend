package com.umust.dobonglife.domain.place.domain.entity;

import com.umust.dobonglife.domain.place.domain.constant.Amenity;

import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Column(columnDefinition = "TEXT")
    private List<String> placeImages;

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

    public void updateRatingInfo(Double newAverageRating, Long newReviewCount) {
        this.averageRating = newAverageRating;
        this.reviewCount = newReviewCount;
    }

    public void applyNewReview(Double newRating) { // TODO: Course 처럼 분리할지 고민
        double totalScore = (this.averageRating * this.reviewCount) + newRating;
        Long reviewCount = this.reviewCount + 1;
        reviewCount = reviewCount + 1;
        this.averageRating = totalScore / reviewCount;
    }
}

