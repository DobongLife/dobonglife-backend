package com.umust.dobonglife.domain.point.presentation;

import com.umust.dobonglife.domain.point.application.PointService;
import com.umust.dobonglife.domain.point.application.dto.MyPointResponse;
import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/point")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/my")
    public ResponseEntity<BaseResponse<MyPointResponse>> getMyPointHistory(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.POINT_HISTORY) int size,
            @RequestParam(defaultValue = "DESC") String order) {
        return ResponseEntity.ok(BaseResponse.ok(pointService.getMyPointHistory(userId, lastId, size, order)));
    }
}
