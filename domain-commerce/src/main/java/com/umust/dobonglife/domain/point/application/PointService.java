package com.umust.dobonglife.domain.point.application;

import com.umust.dobonglife.domain.point.application.dto.MyPointResponse;
import com.umust.dobonglife.domain.point.application.dto.PointHistoryResponse;
import com.umust.dobonglife.domain.point.application.port.in.GetPointUseCase;
import com.umust.dobonglife.domain.point.application.port.in.ManagePointUseCase;
import com.umust.dobonglife.domain.point.application.port.out.LoadPointPort;
import com.umust.dobonglife.domain.point.application.port.out.SavePointPort;
import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.domain.point.domain.entity.PointHistory;
import com.umust.dobonglife.domain.point.exception.PointErrorCode;
import com.umust.dobonglife.domain.point.exception.PointException;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService implements GetPointUseCase, ManagePointUseCase {

    private final LoadPointPort loadPointPort;
    private final SavePointPort savePointPort;

    @Override
    @Transactional(readOnly = true)
    public Long getUserPoint(Long userId) {
        return loadPointPort.findByUserId(userId)
                .map(Point::getBalance)
                .orElse(0L);
    }

    @Override
    @Transactional
    public void deduct(Long userId, Long amount) {
        Point point = loadPointPort.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));
        point.deduct(amount);

        savePointPort.saveHistory(
                PointHistory.ofDeduction(point.getId(), amount, point.getBalance(), "쿠폰 교환"));
    }

    @Override
    @Transactional(readOnly = true)
    public MyPointResponse getMyPointHistory(Long userId, Long lastId, int size, String order) {
        Long totalPoint = getUserPoint(userId);

        Point point = loadPointPort.findByUserId(userId).orElse(null);
        if (point == null) {
            return new MyPointResponse(totalPoint, new CursorResponse<>(List.of(), null, false));
        }

        List<PointHistory> histories = "ASC".equalsIgnoreCase(order)
                ? loadPointPort.findByPointIdAsc(point.getId(), lastId, PageRequest.of(0, size + 1))
                : loadPointPort.findByPointIdDesc(point.getId(), lastId, PageRequest.of(0, size + 1));

        List<PointHistoryResponse> content = histories.stream()
                .map(PointHistoryResponse::from)
                .toList();

        CursorResponse<PointHistoryResponse> cursorResponse = CursorUtils.toCursorResponse(content, size);

        return new MyPointResponse(totalPoint, cursorResponse);
    }

    @Override
    @Transactional
    public void refund(Long userId, Long amount) {
        Point point = loadPointPort.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new PointException(PointErrorCode.POINT_NOT_FOUND));
        point.refund(amount);

        savePointPort.saveHistory(
                PointHistory.ofRefund(point.getId(), amount, point.getBalance(), "쿠폰 교환 취소"));
    }
}
