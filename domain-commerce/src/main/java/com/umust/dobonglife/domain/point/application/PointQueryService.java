package com.umust.dobonglife.domain.point.application;

import com.umust.dobonglife.domain.point.application.dto.MyPointResponse;
import com.umust.dobonglife.domain.point.application.dto.PointHistoryResponse;
import com.umust.dobonglife.domain.point.application.port.in.GetPointUseCase;
import com.umust.dobonglife.domain.point.application.port.out.LoadPointPort;
import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.domain.point.domain.entity.PointHistory;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointQueryService implements GetPointUseCase {

    private final LoadPointPort loadPointPort;

    @Override
    public Long getUserPoint(Long userId) {
        return loadPointPort.findByUserId(userId)
                .map(Point::getBalance)
                .orElse(0L);
    }

    @Override
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
}
