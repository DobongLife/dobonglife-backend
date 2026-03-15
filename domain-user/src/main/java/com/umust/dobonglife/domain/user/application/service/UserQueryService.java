package com.umust.dobonglife.domain.user.application.service;

import com.umust.dobonglife.domain.user.application.port.in.GetUserUseCase;
import com.umust.dobonglife.domain.user.application.port.out.LoadUserPort;
import com.umust.dobonglife.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService implements GetUserUseCase {

    private final LoadUserPort loadUserPort;

    @Override
    public User findById(Long userId) {
        return loadUserPort.loadUser(userId);
    }

    @Override
    public boolean isBlockedUser(Long userId) {
        return loadUserPort.loadUser(userId).isBlocked();
    }

    @Override
    public String getFcmToken(Long userId) {
        return loadUserPort.loadUser(userId).getFcmToken();
    }
}
