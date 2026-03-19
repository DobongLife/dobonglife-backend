package com.umust.dobonglife.global.port.commerce;

import com.umust.dobonglife.global.port.dto.commerce.MyPointInfo;

public interface PointPort {
    Long getUserPoint(Long userId);
    MyPointInfo getMyPointHistory(Long userId, Long lastId, int size, String order);
}
