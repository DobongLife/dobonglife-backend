package com.umust.dobonglife.global.port.commerce;

import java.util.Map;

public interface ExchangePort {
    Map<String, Object> exchange(Long userId, Long promotionId);
}
