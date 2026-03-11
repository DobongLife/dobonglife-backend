package com.umust.dobonglife.domain.promotion.domain.entity;

import com.umust.dobonglife.domain.promotion.domain.constant.DiscountType;
import com.umust.dobonglife.global.common.constant.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "presets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Preset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preset_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Long discountValue;

    private Long minPrice;

    private Long maxPrice;

    private Long point;

    private Long validPeriod;

    @Column(length = 255)
    private String imageUrl;

    @Column(length = 255)
    private String description;
}
