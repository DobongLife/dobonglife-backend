package com.umust.dobonglife.domain.user.infrastructure.adapter;

import com.umust.dobonglife.domain.user.application.port.out.LoadUserPort;
import com.umust.dobonglife.domain.user.application.port.out.SaveUserPort;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.exception.UserErrorCode;
import com.umust.dobonglife.domain.user.exception.UserException;
import com.umust.dobonglife.domain.user.infrastructure.jpa.UserJpaRepository;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements LoadUserPort, SaveUserPort {

    private final UserJpaRepository userJpaRepository;

    // ── LoadUserPort ──

    @Override
    public User loadUser(Long userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    @Override
    public User loadLocalUser(String email) {
        return userJpaRepository.findByEmailAndProvider(email, Provider.LOCAL)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    @Override
    public Optional<User> findByProviderAndProviderId(Provider provider, String providerId) {
        return userJpaRepository.findByProviderAndProviderId(provider, providerId);
    }

    @Override
    public Optional<User> findInactiveByProviderAndProviderId(String provider, String providerId) {
        return userJpaRepository.findInactiveByProviderAndProviderId(provider, providerId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    // ── SaveUserPort ──

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public User saveAndFlush(User user) {
        return userJpaRepository.saveAndFlush(user);
    }

    @Override
    public boolean existsInactiveById(Long userId) {
        return userJpaRepository.existsInactiveById(userId);
    }

    @Override
    public void delete(User user) {
        userJpaRepository.delete(user);
    }
}
