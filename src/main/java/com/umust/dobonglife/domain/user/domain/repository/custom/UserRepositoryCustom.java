package com.umust.dobonglife.domain.user.domain.repository.custom;

public interface UserRepositoryCustom {
    long decreaseBalance(Long userId, Long amount);
}
