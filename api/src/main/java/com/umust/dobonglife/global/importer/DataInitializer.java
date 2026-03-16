package com.umust.dobonglife.global.importer;

import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.auth.security.principal.UserPrincipal;
import com.umust.dobonglife.global.common.constant.Role;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.application.port.out.SaveUserPort;
import com.umust.dobonglife.domain.user.infrastructure.jpa.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("local-db")
public class DataInitializer implements CommandLineRunner {

    private final UserJpaRepository userJpaRepository;
    private final SaveUserPort saveUserPort;
    private final PasswordEncoder passwordEncoder;
    private static final List<GrantedAuthority> MASTER_AUTHORITIES = List.of(
            new SimpleGrantedAuthority("ROLE_ADMIN")
    );

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userJpaRepository.count() == 0) {
            User masterUser = User.builder()
                    .name("master")
                    .email("master@gmail.com")
                    .password(passwordEncoder.encode("1234"))
                    .role(Role.ADMIN)
                    .provider(Provider.LOCAL)
                    .balance(500L)
                    .build();
            saveUserPort.save(masterUser);

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

            // TODO: Point 초기화는 domain-point 모듈 마이그레이션 후 복원
            log.info("master 사용자 계정 생성 및 임시 인증 정보 설정 완료.");
        }
    }
}
