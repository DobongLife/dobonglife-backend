package com.umust.dobonglife.domain.user.domain;

import com.umust.dobonglife.domain.user.domain.User;
import com.umust.dobonglife.global.common.constant.Provider;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    User saveAndFlush(User user);

    Optional<User> findById(Long id);

    void delete(User user);

    Optional<User> findByProviderId(String providerId);

    Optional<User> findByEmailAndProvider(String email, Provider provider);

    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByIdForUpdate(Long userId);

    Optional<User> findInactiveByProviderAndProviderId(String provider, String providerId);

    long count();
}
