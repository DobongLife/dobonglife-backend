package com.umust.dobonglife.domain.user.service.port;

import java.time.Duration;
import java.util.Optional;

public interface VerificationCodeStore {

    void store(String key, String value, Duration ttl);

    Optional<String> find(String key);
}
