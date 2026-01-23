package com.umust.dobonglife.domain.preset.domain;

import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "preset")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Preset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "img", nullable = false)
    private String img;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false)
    private BigDecimal discountValue;

    @Column(name = "min_price", nullable = false)
    private Long minPrice;

    @Column(name = "max_price", nullable = false)
    private Long maxPrice;

    @Column(name = "point", nullable = false)
    private Long point;

    @Column(name = "coupon_period", nullable = false)
    private Long validPeriod;
}
