package com.umust.dobonglife.domain.auth.service;


import com.umust.dobonglife.domain.auth.model.UserPrincipal;
import com.umust.dobonglife.domain.user.model.Role;
import com.umust.dobonglife.domain.user.model.User;
import com.umust.dobonglife.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserPrincipal loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + ": 해당 이메일의 사용자를 찾을 수 없습니다."));

        return UserPrincipal.builder()
                .userId(user.getId())
                .userName(user.getEmail())
                .password(user.getPassword())
                .provider(user.getProvider())
                .authorities(Collections.singleton(user.getRole().toAuthority()))
                .build();
    }
}
