package com.umust.dobonglife.domain.user.infrastructure;

import com.umust.dobonglife.domain.user.domain.User;
import com.umust.dobonglife.domain.user.domain.UserRepository;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public User saveAndFlush(User user) {
        return userJpaRepository.saveAndFlush(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public void delete(User user) {
        userJpaRepository.delete(user);
    }

    @Override
    public Optional<User> findByProviderId(String providerId) {
        return userJpaRepository.findByProviderId(providerId);
    }

    @Override
    public Optional<User> findByEmailAndProvider(String email, Provider provider) {
        return userJpaRepository.findByEmailAndProvider(email, provider);
    }

    @Override
    public Optional<User> findByProviderAndProviderId(Provider provider, String providerId) {
        return userJpaRepository.findByProviderAndProviderId(provider, providerId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByIdForUpdate(Long userId) {
        return userJpaRepository.findByIdForUpdate(userId);
    }

    @Override
    public Optional<User> findInactiveByProviderAndProviderId(String provider, String providerId) {
        return userJpaRepository.findInactiveByProviderAndProviderId(provider, providerId);
    }

    @Override
    public long count() {
        return userJpaRepository.count();
    }
}
