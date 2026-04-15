package com.umust.dobonglife.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisTokenStore implements com.umust.dobonglife.global.port.auth.out.TokenStore {

    private final RedisService redisService;

    @Override
    public void store(String key, String value, Duration ttl) {
        redisService.setValues(key, value, ttl);
    }

    @Override
    public Optional<String> find(String key) {
        return redisService.getValues(key);
    }

    @Override
    public void delete(String key) {
        redisService.delete(key);
    }
}
