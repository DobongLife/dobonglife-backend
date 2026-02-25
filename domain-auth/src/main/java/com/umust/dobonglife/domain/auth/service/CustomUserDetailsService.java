package com.umust.dobonglife.domain.auth.service;

import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.domain.auth.domain.entity.UserPrincipal;
import com.umust.dobonglife.domain.auth.port.AuthUserPort;
import com.umust.dobonglife.domain.auth.port.AuthUserPort.AuthUserInfo;
import com.umust.dobonglife.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthUserPort authUserPort;

    @Override
    public UserPrincipal loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthUserInfo user = authUserPort.findByEmailAndLocalProvider(email)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));

        return UserPrincipal.builder()
                .userId(user.id())
                .userName(user.email())
                .password(user.password())
                .provider(user.provider())
                .authorities(Collections.singleton(user.role().toAuthority()))
                .build();
    }
}
