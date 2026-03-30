package com.umust.dobonglife.domain.exchange.application;

import com.umust.dobonglife.domain.exchange.application.dto.ExchangeRequest;
import com.umust.dobonglife.domain.exchange.application.dto.ExchangeResponse;
import com.umust.dobonglife.domain.exchange.application.port.in.ExchangeUseCase;
import com.umust.dobonglife.domain.coupon.application.port.in.ManageCouponUseCase;
import com.umust.dobonglife.domain.exchange.application.port.out.SaveExchangeSagaPort;
import com.umust.dobonglife.domain.exchange.domain.entity.ExchangeSaga;
import com.umust.dobonglife.domain.exchange.domain.vo.SagaStatus;
import com.umust.dobonglife.domain.point.application.port.in.ManagePointUseCase;
import com.umust.dobonglife.domain.promotion.application.port.in.GetPromotionUseCase;
import com.umust.dobonglife.domain.promotion.application.port.in.ManagePromotionUseCase;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.global.port.user.in.ManageUserUseCase;
import com.umust.dobonglife.global.common.event.CouponIssuedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeOrchestrator implements ExchangeUseCase {

    private final SaveExchangeSagaPort saveExchangeSagaPort;
    private final ManageUserUseCase manageUserUseCase;
    private final ManagePointUseCase managePointUseCase;
    private final GetPromotionUseCase getPromotionUseCase;
    private final ManagePromotionUseCase managePromotionUseCase;
    private final ManageCouponUseCase manageCouponUseCase;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ExchangeResponse execute(ExchangeRequest request) {

        ExchangeSaga saga = ExchangeSaga.create(request.userId(), request.promotionId());
        saveExchangeSagaPort.save(saga);

        try {
            manageUserUseCase.canExchangeCoupon(request.userId());
            saga.markUserValidated();

            Promotion promotion = getPromotionUseCase.getActivePromotion(request.promotionId());
            saga.setPointAmount(promotion.getPoint());

            managePointUseCase.deduct(request.userId(), promotion.getPoint());
            saga.markPointDeducted();

            managePromotionUseCase.deductStock(request.promotionId());
            saga.markStockDeducted();

            Long couponId = manageCouponUseCase.issue(request.userId(), promotion.getId(), promotion.getCouponValidDays());
            saga.markCouponIssued(couponId);
            saga.complete();
            saveExchangeSagaPort.save(saga);

        } catch (Exception e) {
            compensate(saga);
            saga.fail(e.getMessage());
            saveExchangeSagaPort.save(saga);
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
            managePointUseCase.refund(saga.getUserId(), saga.getPointAmount());
            saga.markPointRefunded();
        } catch (Exception e) {
            log.error("포인트 환불 보상 실패: sagaId={}", saga.getId(), e);
        }
    }

    private void compensateStock(ExchangeSaga saga) {
        try {
            saga.markStockRestoring();
            managePromotionUseCase.restoreStock(saga.getPromotionId());
            saga.markStockRestored();
        } catch (Exception e) {
            log.error("재고 복원 보상 실패: sagaId={}", saga.getId(), e);
        }
    }
}
