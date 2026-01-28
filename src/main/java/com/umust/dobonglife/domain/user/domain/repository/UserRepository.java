package com.umust.dobonglife.domain.user.domain.repository;

import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.custom.UserRepositoryCustom;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByProviderId(String providerId);
    Optional<User> findByEmailAndProvider(String email, Provider provider);
    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);
    long findBalanceById (Long userId);
    Optional<User> findByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :userId")
    Optional<User> findByIdForUpdate(@Param("userId") Long userId);
}
