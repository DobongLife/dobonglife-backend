package com.umust.dobonglife.domain.coupon.domain.entity;

import com.umust.dobonglife.domain.coupon.controller.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.domain.constant.PromotionType;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.global.auth.CouponCodeGenerator;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import jakarta.persistence.*;
        import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Table(name = "promotion")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private PromotionType category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "promotion_images", joinColumns = @JoinColumn(name = "promotion_id"))
    @Column(name = "img_urls", nullable = false)
    private List<String> imgUrls;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false)
    private BigDecimal discountValue;

    @Column(name = "min_price", nullable = false)
    private Long minPrice;

    @Column(name = "max_price", nullable = false)
    private Long maxPrice;

    @Column(name = "code")
    private String code;

    @Column(name = "point", nullable = false)
    private Long point;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "coupon_period", nullable = false)
    private Long validPeriod;

    @Column(name = "businesses_id", nullable = false)
    private Long businessesId;

    @Builder
    public Promotion(PromotionType category, Place place, String title, String description, List<String> imgUrls,
                     DiscountType discountType, BigDecimal discountValue, Long minPrice,
                     Long maxPrice, String code, Long point, LocalDate startDate,
                     LocalDate endDate, Long validPeriod, Long businessesId) {

        validate(discountType, discountValue, minPrice, maxPrice, code, startDate, endDate);

        this.category = category;
        this.place = place;
        this.title = title;
        this.description = description;
        this.imgUrls = (imgUrls == null || imgUrls.isEmpty()) ? Collections.singletonList(category.getImageUrl()) : imgUrls;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.code = code;
        this.point = point;
        this.startDate = startDate;
        this.endDate = endDate;
        this.validPeriod = validPeriod;
        this.businessesId = businessesId;
    }

    private void validate(DiscountType discountType, BigDecimal discountValue,
                          Long minPrice, Long maxPrice, String code,
                          LocalDate startDate, LocalDate endDate) {

        if (code == null || code.length() != 6) {
            throw new BusinessException(ErrorCode.INVALID_COUPON_CODE);
        }

        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_DISCOUNT_VALUE);
        }

        if ((minPrice != null && minPrice < 0) || (maxPrice != null && maxPrice < 0)) {
            throw new BusinessException(ErrorCode.INVALID_VALUE);
        }

        if (discountType == DiscountType.PERCENT) {
            if (discountValue.compareTo(new BigDecimal("100")) > 0) {
                throw new BusinessException(ErrorCode.INVALID_DISCOUNT_VALUE);
            }
        }
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);
            }
        }
    }

    public static Promotion createPromotion(PromotionRegisterRequest dto, List<String> imgUrl, Long managerId, Place place) {
        PromotionType type = PromotionType.valueOf(dto.category());

        return Promotion.builder()
                .category(type)
                .place(place)
                .title(dto.couponName())
                .description(dto.couponDescription())
                .imgUrls(imgUrl.isEmpty() ? Collections.singletonList(type.getImageUrl()) : imgUrl)
                .discountType(DiscountType.valueOf(dto.discountType()))
                .discountValue(BigDecimal.valueOf(dto.discountValue()))
                .minPrice(dto.minPurchaseAmount() != null ? dto.minPurchaseAmount().longValue() : null)
                .maxPrice(dto.maxDiscountAmount() != null ? dto.maxDiscountAmount().longValue() : null)
                .code(CouponCodeGenerator.generate())
                .point(0L)
                .startDate(dto.issueStartDate())
                .endDate(dto.issueEndDate())
                .validPeriod(dto.validityDays() != null ? dto.validityDays().longValue() : null)
                .businessesId(managerId)
                .build();
    }
}
