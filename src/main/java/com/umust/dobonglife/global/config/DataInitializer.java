package com.umust.dobonglife.global.config;

import com.umust.dobonglife.domain.auth.model.Provider;
import com.umust.dobonglife.domain.auth.model.UserPrincipal;
import com.umust.dobonglife.domain.user.model.Role;
import com.umust.dobonglife.domain.user.model.User;
import com.umust.dobonglife.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("local-db")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private static final List<GrantedAuthority> MASTER_AUTHORITIES = List.of(
            new SimpleGrantedAuthority("ROLE_ADMIN")
    );

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User masterUser = User.builder()
                    .name("master")
                    .role(Role.ADMIN)
                    .provider(Provider.LOCAL)
                    .build();
            userRepository.save(masterUser);

            UserPrincipal principal = UserPrincipal.builder()
                    .userId(masterUser.getId())
                    .userName(masterUser.getName())
                    .role(masterUser.getRole())
                    .provider(masterUser.getProvider())
                    .authorities(MASTER_AUTHORITIES)
                    .build();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    MASTER_AUTHORITIES
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("master 사용자 계정 생성 및 임시 인증 정보 설정 완료.");
        }

    }
}