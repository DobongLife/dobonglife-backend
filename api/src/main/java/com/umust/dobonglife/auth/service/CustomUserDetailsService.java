package com.umust.dobonglife.auth.service;

import com.umust.dobonglife.auth.security.principal.UserPrincipal;
import com.umust.dobonglife.global.port.user.dto.LocalLoginUser;
import com.umust.dobonglife.global.port.user.in.LoadLocalAuthUserUseCase;
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

    private final LoadLocalAuthUserUseCase loadLocalAuthUserUseCase;

    @Override
    public UserPrincipal loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            LocalLoginUser userInfo = loadLocalAuthUserUseCase.loadLocalUserByEmail(email);
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
