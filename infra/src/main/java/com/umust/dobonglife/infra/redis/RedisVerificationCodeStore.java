package com.umust.dobonglife.infra.redis;

import com.umust.dobonglife.domain.user.application.port.out.VerificationCodeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisVerificationCodeStore implements VerificationCodeStore {

    private final RedisService redisService;

    @Override
    public void store(String key, String value, Duration ttl) {
        redisService.setValues(key, value, ttl);
    }

    @Override
    public Optional<String> find(String key) {
        return redisService.getValues(key);
    }
}
