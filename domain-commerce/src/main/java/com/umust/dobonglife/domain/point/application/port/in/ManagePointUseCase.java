package com.umust.dobonglife.domain.point.application.port.in;

public interface ManagePointUseCase {

    void deduct(Long userId, Long amount);

    void refund(Long userId, Long amount);
}
