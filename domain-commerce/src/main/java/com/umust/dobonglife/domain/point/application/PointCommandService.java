package com.umust.dobonglife.domain.point.application;

import com.umust.dobonglife.domain.point.application.port.in.ManagePointUseCase;
import com.umust.dobonglife.domain.point.application.port.in.PointCleanupUseCase;
import com.umust.dobonglife.domain.point.application.port.in.PointRestoreUseCase;
import com.umust.dobonglife.domain.point.application.port.out.LoadPointPort;
import com.umust.dobonglife.domain.point.application.port.out.SavePointPort;
import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.domain.point.domain.entity.PointHistory;
import com.umust.dobonglife.domain.point.exception.PointErrorCode;
import com.umust.dobonglife.domain.point.exception.PointException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PointCommandService implements ManagePointUseCase, PointCleanupUseCase, PointRestoreUseCase {

    private final LoadPointPort loadPointPort;
    private final SavePointPort savePointPort;

    @Override
    public void deduct(Long userId, Long amount) {
        Point point = loadPointPort.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));
        point.deduct(amount);

        savePointPort.saveHistory(
                PointHistory.ofDeduction(point.getId(), amount, point.getBalance(), "쿠폰 교환"));
    }

    @Override
    public void refund(Long userId, Long amount) {
        Point point = loadPointPort.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));
        point.refund(amount);

        savePointPort.saveHistory(
                PointHistory.ofRefund(point.getId(), amount, point.getBalance(), "쿠폰 교환 취소"));
    }

    // TODO: 회원탈퇴 Orchestrator 연동 시 Port에 cleanup/restore 메서드 추가 필요
    @Override
    public void markPendingByUserId(Long userId) {
        throw new UnsupportedOperationException("Port 기반 cleanup 미구현 — 회원탈퇴 Orchestrator 연동 시 구현 필요");
    }

    @Override
    public void finalizeByUserId(Long userId) {
        throw new UnsupportedOperationException("Port 기반 cleanup 미구현 — 회원탈퇴 Orchestrator 연동 시 구현 필요");
    }

    @Override
    public void restoreByUserId(Long userId) {
        throw new UnsupportedOperationException("Port 기반 restore 미구현 — 회원탈퇴 Orchestrator 연동 시 구현 필요");
    }
}
