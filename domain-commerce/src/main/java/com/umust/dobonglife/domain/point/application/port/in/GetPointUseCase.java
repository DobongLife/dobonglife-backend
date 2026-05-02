package com.umust.dobonglife.domain.point.application.port.in;

import com.umust.dobonglife.domain.point.application.dto.MyPointResponse;

public interface GetPointUseCase {

    Long getUserPoint(Long userId);

    MyPointResponse getMyPointHistory(Long userId, Long lastId, int size, String order);
}
