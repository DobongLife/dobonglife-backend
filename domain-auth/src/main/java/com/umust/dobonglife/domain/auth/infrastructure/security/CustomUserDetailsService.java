package com.umust.dobonglife.domain.auth.infrastructure.security;

import com.umust.dobonglife.domain.auth.application.port.out.AuthUserPort;
import com.umust.dobonglife.domain.auth.domain.AuthUserInfo;
import com.umust.dobonglife.domain.auth.domain.UserPrincipal;
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
        try {
            AuthUserInfo userInfo = authUserPort.loadLocalUserByEmail(email);
            return UserPrincipal.builder()
                    .userId(userInfo.userId())
                    .userName(userInfo.email())
                    .password(userInfo.password())
                    .provider(userInfo.provider())
                    .authorities(Collections.singleton(userInfo.role().toAuthority()))
                    .build();
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
