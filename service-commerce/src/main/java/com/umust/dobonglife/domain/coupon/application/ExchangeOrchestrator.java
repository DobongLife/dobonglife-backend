package com.umust.dobonglife.domain.coupon.application;

import com.umust.dobonglife.domain.coupon.application.dto.ExchangeRequest;
import com.umust.dobonglife.domain.coupon.application.dto.ExchangeResponse;
import com.umust.dobonglife.domain.coupon.domain.entity.ExchangeSaga;
import com.umust.dobonglife.domain.coupon.domain.repository.ExchangeSagaRepository;
import com.umust.dobonglife.domain.coupon.domain.vo.SagaStatus;
import com.umust.dobonglife.domain.point.application.PointService;
import com.umust.dobonglife.domain.promotion.application.PromotionService;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.global.common.event.CouponIssuedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeOrchestrator {

    private final ExchangeSagaRepository sagaRepository;
    private final ManageUserUseCase manageUserUseCase;
    private final PointService pointService;
    private final PromotionService promotionService;
    private final CouponService couponService;
    private final ApplicationEventPublisher eventPublisher;

    public ExchangeResponse execute(ExchangeRequest request) {

        ExchangeSaga saga = ExchangeSaga.create(request.userId(), request.promotionId());
        sagaRepository.save(saga);

        try {
            manageUserUseCase.canExchangeCoupon(request.userId());
            saga.markUserValidated();

            Promotion promotion = promotionService.getActivePromotion(request.promotionId());
            saga.setPointAmount(promotion.getPoint());

            pointService.deduct(request.userId(), promotion.getPoint());
            saga.markPointDeducted();

            promotionService.deductStock(request.promotionId());
            saga.markStockDeducted();

            Long couponId = couponService.issue(request.userId(), promotion.getId(), promotion.getCouponValidDays());
            saga.markCouponIssued(couponId);
            saga.complete();
            sagaRepository.save(saga);

        } catch (Exception e) {
            compensate(saga);
            saga.fail(e.getMessage());
            sagaRepository.save(saga);
            throw e;
        }

        eventPublisher.publishEvent(CouponIssuedEvent.of(
                saga.getId(), saga.getUserId(), saga.getPromotionId(),
                saga.getCouponId(), saga.getPointAmount()));

        return ExchangeResponse.from(saga);
    }

    private void compensate(ExchangeSaga saga) {
        SagaStatus status = saga.getSagaStatus();

        if (status == SagaStatus.STOCK_DEDUCTED || status == SagaStatus.COUPON_ISSUED) {
            compensateStock(saga);
        }
        if (status == SagaStatus.POINT_DEDUCTED || status == SagaStatus.STOCK_DEDUCTED || status == SagaStatus.COUPON_ISSUED) {
            compensatePoint(saga);
        }
    }

    private void compensatePoint(ExchangeSaga saga) {
        try {
            saga.markPointRefunding();
            pointService.refund(saga.getUserId(), saga.getPointAmount());
            saga.markPointRefunded();
        } catch (Exception e) {
            log.error("포인트 환불 보상 실패: sagaId={}", saga.getId(), e);
        }
    }

    private void compensateStock(ExchangeSaga saga) {
        try {
            saga.markStockRestoring();
            promotionService.restoreStock(saga.getPromotionId());
            saga.markStockRestored();
        } catch (Exception e) {
            log.error("재고 복원 보상 실패: sagaId={}", saga.getId(), e);
        }
    }
}
