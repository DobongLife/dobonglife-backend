package com.umust.dobonglife.global.port.dto.commerce;

import com.umust.dobonglife.global.common.response.CursorResponse;

public record MyPointInfo(
        Long totalPoint,
        CursorResponse<PointHistoryInfo> pointHistory
) {}
