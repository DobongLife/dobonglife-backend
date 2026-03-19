package com.umust.dobonglife.domain.place.domain.entity;

import com.umust.dobonglife.domain.place.domain.vo.Theme;
import com.umust.dobonglife.domain.place.exception.PlaceErrorCode;
import com.umust.dobonglife.domain.place.exception.PlaceException;
import com.umust.dobonglife.global.common.constant.Category;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "places")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;
    private Double latitude;
    private Double longitude;
    @Column(columnDefinition = "DECIMAL(2,1)")
    private Double averageRating;
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double ratingSum;
    private Long reviewCount;

    @Column(length = 30)
    private String name;

    private String thumbnailUrl;

    @BatchSize(size = 50)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "place_id", nullable = false)
    @OrderBy("sortOrder ASC")
    private List<PlaceImage> images = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "place_detail_id")
    private PlaceDetail detail;

    @BatchSize(size = 50)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "place_id", nullable = false)
    private List<PlaceTheme> themes = new ArrayList<>();

    @Builder
    private Place(
            Category category,
            String name,
            Double latitude,
            Double longitude
    ) {
        this.category = category;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.averageRating = 0.0;
        this.ratingSum = 0.0;
        this.reviewCount = 0L;
    }

    public void attachImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            throw new PlaceException(PlaceErrorCode.PLACE_IMAGE_REQUIRED);
        }
        this.thumbnailUrl = imageUrls.get(0);
        this.images.addAll(PlaceImage.ofUrls(imageUrls));
    }

    public void attachDetail(PlaceDetail detail) {
        this.detail = detail;
    }

    public void attachThemes(List<Theme> themeList) {
        List<PlaceTheme> placeThemes = themeList.stream()
                .map(PlaceTheme::of)
                .toList();
        this.themes.addAll(placeThemes);
    }

    public void addReview(Double rating) {
        this.reviewCount++;
        this.ratingSum += rating;
        this.averageRating = Math.round(this.ratingSum / this.reviewCount * 10.0) / 10.0;
    }

    public void removeReview(Double rating) {
        if (this.reviewCount <= 0) return;
        this.reviewCount--;
        this.ratingSum -= rating;
        this.averageRating = this.reviewCount == 0 ? 0.0
                : Math.round(this.ratingSum / this.reviewCount * 10.0) / 10.0;
    }
}
