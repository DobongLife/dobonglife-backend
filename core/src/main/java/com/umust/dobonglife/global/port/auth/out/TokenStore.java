package com.umust.dobonglife.global.port.auth.out;

import java.time.Duration;
import java.util.Optional;

public interface TokenStore {
    void store(String key, String value, Duration ttl);
    Optional<String> find(String key);
    void delete(String key);
}
