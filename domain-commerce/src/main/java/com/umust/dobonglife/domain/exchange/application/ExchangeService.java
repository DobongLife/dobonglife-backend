package com.umust.dobonglife.domain.exchange.application;

import com.umust.dobonglife.domain.exchange.application.dto.ExchangeRequest;
import com.umust.dobonglife.domain.exchange.application.dto.ExchangeResponse;
import com.umust.dobonglife.domain.exchange.application.port.in.ExchangeUseCase;
import com.umust.dobonglife.domain.coupon.application.port.in.ManageCouponUseCase;
import com.umust.dobonglife.domain.point.application.port.in.ManagePointUseCase;
import com.umust.dobonglife.domain.promotion.application.port.in.GetPromotionUseCase;
import com.umust.dobonglife.domain.promotion.application.port.in.ManagePromotionUseCase;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.global.common.event.CouponIssuedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExchangeService implements ExchangeUseCase {

    private final ManagePointUseCase managePointUseCase;
    private final GetPromotionUseCase getPromotionUseCase;
    private final ManagePromotionUseCase managePromotionUseCase;
    private final ManageCouponUseCase manageCouponUseCase;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ExchangeResponse execute(ExchangeRequest request) {
        Promotion promotion = getPromotionUseCase.getActivePromotion(request.promotionId());

        managePointUseCase.deduct(request.userId(), promotion.getPoint());
        managePromotionUseCase.deductStock(request.promotionId());
        Long couponId = manageCouponUseCase.issue(request.userId(), promotion.getId(), promotion.getCouponValidDays());

        eventPublisher.publishEvent(CouponIssuedEvent.of(
                request.userId(), request.promotionId(), couponId, promotion.getPoint()));

        return new ExchangeResponse(couponId, promotion.getPoint());
    }
}
