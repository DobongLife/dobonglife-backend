package com.umust.dobonglife.application.auth.adapter;

import com.umust.dobonglife.domain.auth.application.port.out.AuthUserPort;
import com.umust.dobonglife.domain.auth.domain.AuthUserInfo;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.OAuthUserUseCase;
import com.umust.dobonglife.domain.user.application.port.out.LoadUserPort;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUserPortAdapter implements AuthUserPort {

    private final LoadUserPort loadUserPort;
    private final OAuthUserUseCase oAuthUserUseCase;
    private final ManageUserUseCase manageUserUseCase;

    @Override
    public AuthUserInfo loadLocalUserByEmail(String email) {
        User user = loadUserPort.loadLocalUser(email);
        return toAuthUserInfo(user);
    }

    @Override
    public AuthUserInfo findOrCreateOAuthUser(Provider provider, String providerId, String email, String name) {
        User user = oAuthUserUseCase.findOrCreateOAuthUser(provider, providerId, email, name);
        return toAuthUserInfo(user);
    }

    @Override
    public void updateFcmToken(Long userId, String fcmToken) {
        manageUserUseCase.updateFcmToken(userId, fcmToken);
    }

    private AuthUserInfo toAuthUserInfo(User user) {
        return new AuthUserInfo(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getProvider(),
                user.getRole()
        );
    }
}
