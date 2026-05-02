package com.umust.dobonglife.domain.point.application.dto;

import com.umust.dobonglife.global.common.response.CursorResponse;

public record MyPointResponse(
        Long totalPoint,
        CursorResponse<PointHistoryResponse> pointHistory
) {}
