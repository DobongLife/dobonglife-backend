package com.umust.dobonglife.global.port;

public interface PointPort {

    void deduct(Long sagaId, Long userId, Long amount);

    void refund(Long sagaId, Long userId, Long amount);
}
