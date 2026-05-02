package com.umust.dobonglife.global.port.user.out;

import java.time.Duration;
import java.util.Optional;

public interface VerificationCodeStore {
    void store(String key, String value, Duration ttl);
    Optional<String> find(String key);
}
