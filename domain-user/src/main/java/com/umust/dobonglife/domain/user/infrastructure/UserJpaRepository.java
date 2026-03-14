package com.umust.dobonglife.domain.user.infrastructure;

import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.constant.Provider;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderId(String providerId);

    Optional<User> findByEmailAndProvider(String email, Provider provider);

    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :userId")
    Optional<User> findByIdForUpdate(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM users WHERE provider = :provider AND provider_id = :providerId AND status = 'INACTIVE' ORDER BY updated_at DESC LIMIT 1", nativeQuery = true)
    Optional<User> findInactiveByProviderAndProviderId(@Param("provider") String provider, @Param("providerId") String providerId);
}
