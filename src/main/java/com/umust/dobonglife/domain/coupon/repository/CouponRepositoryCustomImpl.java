package com.umust.dobonglife.domain.coupon.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.domain.repository.custom.CouponRepositoryCustom;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.CouponItem;
import com.umust.dobonglife.domain.coupon.presentation.dto.response.MyCouponStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.umust.dobonglife.domain.coupon.domain.entity.QCoupon.coupon;
import static com.umust.dobonglife.domain.coupon.domain.entity.QPromotion.promotion;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryCustomImpl implements CouponRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    @Override
    public List<CouponItem> getCouponItemList(Long userId) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        return queryFactory
                .select(Projections.constructor(
                        CouponItem.class,
                        coupon.id,
                        promotion.category,
                        promotion.title,
                        promotion.description,
                        promotion.img,
                        promotion.discountType,
                        promotion.discountValue,
                        promotion.minPrice,
                        promotion.maxPrice,
                        promotion.endDate,
                        coupon.couponStatus
                ))
                .from(coupon)
                .join(promotion).on(coupon.promotionId.eq(promotion.id))
                .where(
                        coupon.userId.eq(userId),
                        // 만료 쿠폰은 최근 30일 이내만 조회
                        coupon.couponStatus.eq(CouponStatus.EXPIRED)
                                .and(coupon.issueEndDate.goe(thirtyDaysAgo))
                                .or(coupon.couponStatus.ne(CouponStatus.EXPIRED))
                )
                .orderBy(
                        // 사용 가능 > 사용됨 > 만료됨 순서로 함
                        new CaseBuilder()
                                .when(coupon.couponStatus.eq(CouponStatus.AVAILABLE)).then(1)
                                .when(coupon.couponStatus.eq(CouponStatus.USED)).then(2)
                                .otherwise(3)
                                .asc(),
                        // 같은 상태 내에서는 만료일 임박 순
                        promotion.endDate.asc()
                )
                .fetch();
    }

    @Override
    public MyCouponStatus getMyCouponStatus(Long userId) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        // 한 번의 쿼리로 모든 상태 카운트 조회
        var result = queryFactory
                .select(
                        // 사용 가능한 쿠폰 수
                        coupon.couponStatus.when(CouponStatus.AVAILABLE).then(1).otherwise(0).sum(),
                        // 사용된 쿠폰 수
                        coupon.couponStatus.when(CouponStatus.USED).then(1).otherwise(0).sum(),
                        // 만료된 쿠폰 수 (최근 30일 이내)
                        new CaseBuilder()
                                .when(coupon.couponStatus.eq(CouponStatus.EXPIRED)
                                        .and(coupon.issueEndDate.goe(thirtyDaysAgo)))
                                .then(1)
                                .otherwise(0)
                                .sum()
                )
                .from(coupon)
                .where(coupon.userId.eq(userId))
                .fetchOne();

        if (result == null) {
            return new MyCouponStatus(0, 0, 0);
        }

        Integer available = result.get(0, Integer.class);
        Integer used = result.get(1, Integer.class);
        Integer expired = result.get(2, Integer.class);

        return new MyCouponStatus(
                available != null ? available : 0,
                used != null ? used : 0,
                expired != null ? expired : 0
        );
    }
}
