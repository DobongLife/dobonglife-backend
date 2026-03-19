package com.umust.dobonglife.domain.point.presentation;

import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.port.commerce.PointPort;
import com.umust.dobonglife.global.port.dto.commerce.MyPointInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/point")
@RequiredArgsConstructor
public class PointController {

    private final PointPort pointPort;

    @GetMapping("/my")
    public ResponseEntity<BaseResponse<MyPointInfo>> getMyPointHistory(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.POINT_HISTORY) int size,
            @RequestParam(defaultValue = "DESC") String order) {
        return ResponseEntity.ok(BaseResponse.ok(pointPort.getMyPointHistory(userId, lastId, size, order)));
    }
}
