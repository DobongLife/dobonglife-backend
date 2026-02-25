package com.umust.dobonglife.global.auth;

import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.auth.domain.entity.UserPrincipal;
import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.domain.point.domain.repository.PointRepository;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final PasswordEncoder passwordEncoder;
    private static final List<GrantedAuthority> MASTER_AUTHORITIES = List.of(
            new SimpleGrantedAuthority("ROLE_ADMIN")
    );

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User masterUser = User.builder()
                    .name("master")
                    .email("master@gmail.com")
                    .password(passwordEncoder.encode("1234"))
                    .role(Role.ADMIN)
                    .provider(Provider.LOCAL)
                    .balance(500L)
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

            Point signupPoint = Point.builder()
                    .user(masterUser)
                    .amount(1_000L)
                    .title("회원가입 보너스")
                    .afterBalance(1_000L)
                    .isUsed(false)
                    .build();

            Point eventPoint = Point.builder()
                    .user(masterUser)
                    .amount(500L)
                    .title("이벤트 참여 보상")
                    .afterBalance(1_500L)
                    .isUsed(false)
                    .build();

            pointRepository.saveAll(List.of(signupPoint, eventPoint));

            log.info("master 사용자 계정 생성 및 임시 인증 정보 설정 완료.");
        }
    }
}
