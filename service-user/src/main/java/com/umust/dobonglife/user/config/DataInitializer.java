package com.umust.dobonglife.user.config;

import com.umust.dobonglife.domain.user.application.port.out.LoadUserPort;
import com.umust.dobonglife.domain.user.application.port.out.SaveUserPort;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String DEFAULT_EMAIL = "tigerjong100@gmail.com";
    private static final String DEFAULT_NAME = "신종윤";
    private static final String DEFAULT_PASSWORD = "1234";

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (loadUserPort.existsByEmail(DEFAULT_EMAIL)) {
            return;
        }

        User manager = User.builder()
                .email(DEFAULT_EMAIL)
                .name(DEFAULT_NAME)
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .role(Role.MANAGER)
                .provider(Provider.LOCAL)
                .build();

        saveUserPort.save(manager);
        log.info("[DataInitializer] 기본 MANAGER 계정 생성 완료: {}", DEFAULT_EMAIL);
    }
}
