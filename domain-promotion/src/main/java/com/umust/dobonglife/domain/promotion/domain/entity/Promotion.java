package com.umust.dobonglife.domain.promotion.domain.entity;

import com.umust.dobonglife.domain.promotion.domain.constant.DiscountType;
import com.umust.dobonglife.domain.promotion.exception.PromotionErrorCode;
import com.umust.dobonglife.domain.promotion.exception.PromotionException;
import com.umust.dobonglife.global.common.constant.Category;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "promotions")
public class Promotion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long id;

    private Long businessId;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Category category;

    @Column(nullable = true)
    private Integer priority;

    @Column(length = 6)
    private String code;

    @Column(length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer couponValidDays;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private DiscountType discountType;

    private Long discountValue;

    private Long minPrice;

    private Long maxPrice;

    private Long point;

    private Long totalQuantity;

    private Long issuedCount;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "thumbnail_id")
    private PromotionImage thumbnail;

    @BatchSize(size = 50)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "promotion_id", nullable = false)
    @OrderBy("sortOrder ASC")
    private List<PromotionImage> images = new ArrayList<>();

    @Builder
    private Promotion(
            Long businessId,
            Category category,
            Integer priority,
            String code,
            String title,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            Integer couponValidDays,
            DiscountType discountType,
            Long discountValue,
            Long minPrice,
            Long maxPrice,
            Long point,
            Long totalQuantity
    ) {
        validateDiscount(discountType, discountValue);
        validatePrice(minPrice, maxPrice);
        validatePeriod(startDate, endDate);

        this.businessId = businessId;
        this.category = category;
        this.priority = priority;
        this.code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.couponValidDays = couponValidDays;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.point = point;
        this.totalQuantity = totalQuantity;
        this.issuedCount = 0L;
    }

    private void validateDiscount(DiscountType type, Long value) {
        if (value == null || value < 0) {
            throw new PromotionException(PromotionErrorCode.INVALID_DISCOUNT_VALUE);
        }
        if (type == DiscountType.PERCENT && value > 100) {
            throw new PromotionException(PromotionErrorCode.DISCOUNT_PERCENT_EXCEEDED);
        }
    }

    private void validatePrice(Long minPrice, Long maxPrice) {
        if (minPrice != null && minPrice < 0) {
            throw new PromotionException(PromotionErrorCode.NEGATIVE_PRICE);
        }
        if (maxPrice != null && maxPrice < 0) {
            throw new PromotionException(PromotionErrorCode.NEGATIVE_PRICE);
        }
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new PromotionException(PromotionErrorCode.MIN_PRICE_EXCEEDS_MAX);
        }
    }

    private void validatePeriod(LocalDate start, LocalDate end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new PromotionException(PromotionErrorCode.INVALID_DATE_RANGE);
        }
    }

    public void attachImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        List<PromotionImage> promotionImages = PromotionImage.ofUrls(imageUrls);
        this.thumbnail = promotionImages.get(0);
        this.images.addAll(promotionImages);
    }

    public void update(String title, String description, Long totalQuantity) {
        if (totalQuantity < this.issuedCount) {
            throw new PromotionException(PromotionErrorCode.QUANTITY_BELOW_ISSUED);
        }
        this.title = title;
        this.description = description;
        this.totalQuantity = totalQuantity;
    }

    public void validateActive() {
        LocalDate today = LocalDate.now();
        if (today.isBefore(this.startDate) || today.isAfter(this.endDate)) {
            throw new PromotionException(PromotionErrorCode.PROMOTION_PERIOD_EXPIRED);
        }
    }

    public void deductStock() {
        if (this.issuedCount >= this.totalQuantity) {
            throw new PromotionException(PromotionErrorCode.COUPON_SOLD_OUT);
        }
        this.issuedCount++;
    }

    public void restoreStock() {
        if (this.issuedCount > 0) {
            this.issuedCount--;
        }
    }

    public void validateCode(String code) {
        if(!this.code.equals(code))
            throw new PromotionException(PromotionErrorCode.INVALID_COUPON_CODE);
    }
}
