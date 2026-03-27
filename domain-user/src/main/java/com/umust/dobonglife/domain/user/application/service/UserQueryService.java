package com.umust.dobonglife.domain.user.application.service;

import com.umust.dobonglife.domain.user.application.dto.LocalLoginUser;
import com.umust.dobonglife.domain.user.application.port.in.GetUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.LoadLocalAuthUserUseCase;
import com.umust.dobonglife.domain.user.application.port.out.LoadUserPort;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService implements GetUserUseCase, LoadLocalAuthUserUseCase {

    private final LoadUserPort loadUserPort;

    @Override
    public String getProviderId(Long userId) {return loadUserPort.loadUser(userId).getProviderId();}

    @Override
    public Provider getProvider(Long userId) {return loadUserPort.loadUser(userId).getProvider();}

    @Override
    public boolean isBlockedUser(Long userId) {
        return loadUserPort.loadUser(userId).isBlocked();
    }

    @Override
    public boolean isInactiveUser(Long userId) {
        return loadUserPort.existsInactiveById(userId);
    }

    @Override
    public String getFcmToken(Long userId) {
        return loadUserPort.loadUser(userId).getFcmToken();
    }

    @Override
    public LocalLoginUser loadLocalUserByEmail(String email) {
        User user = loadUserPort.loadLocalUser(email);

        return new LocalLoginUser(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getProvider(),
                user.getRole()
        );
    }
}
