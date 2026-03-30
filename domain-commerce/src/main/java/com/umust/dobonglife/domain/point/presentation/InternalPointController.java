package com.umust.dobonglife.domain.point.presentation;

import com.umust.dobonglife.domain.point.application.port.in.GetPointUseCase;
import com.umust.dobonglife.domain.point.application.dto.MyPointResponse;
import com.umust.dobonglife.domain.point.application.dto.PointHistoryResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.port.dto.commerce.MyPointInfo;
import com.umust.dobonglife.global.port.dto.commerce.PointHistoryInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/point")
@RequiredArgsConstructor
public class InternalPointController {

    private final GetPointUseCase getPointUseCase;

    @GetMapping("/balance/{userId}")
    public Long getUserPoint(@PathVariable Long userId) {
        return getPointUseCase.getUserPoint(userId);
    }

    @GetMapping("/my")
    public MyPointInfo getMyPointHistory(
            @RequestParam Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam int size,
            @RequestParam(defaultValue = "DESC") String order) {
        MyPointResponse response = getPointUseCase.getMyPointHistory(userId, lastId, size, order);

        List<PointHistoryInfo> content = response.pointHistory().getContent().stream()
                .map(h -> new PointHistoryInfo(
                        h.pointHistoryId(), h.title(), h.amount(),
                        h.afterBalance(), h.isUsed(), h.createdAt()))
                .toList();

        return new MyPointInfo(
                response.totalPoint(),
                new CursorResponse<>(content, response.pointHistory().getLastId(), response.pointHistory().isHasNext())
        );
    }
}
