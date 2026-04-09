package com.umust.dobonglife.domain.user.application.port.out;

import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.constant.Provider;

import java.util.Optional;

public interface LoadUserPort {

    User loadUser(Long userId);

    User loadLocalUser(String email);

    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

    Optional<User> findInactiveByProviderAndProviderId(String provider, String providerId);

    boolean existsByEmail(String email);

    boolean existsNotActiveById(Long userId);
}
