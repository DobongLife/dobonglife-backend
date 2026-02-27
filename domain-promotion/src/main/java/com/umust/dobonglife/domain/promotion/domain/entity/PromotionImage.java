package com.umust.dobonglife.domain.promotion.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "promotion_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_image_id")
    private Long id;

    @Column(length = 255, nullable = false)
    private String imageUrl;

    private Short sortOrder;

    @Builder
    private PromotionImage(String imageUrl, Short sortOrder) {
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }
}
