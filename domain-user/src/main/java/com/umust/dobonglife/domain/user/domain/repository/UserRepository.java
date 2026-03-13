package com.umust.dobonglife.domain.user.domain.repository;

import com.umust.dobonglife.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.isBlocked FROM User u WHERE u.id = :userId")
    boolean isUserBlocked(@Param("userId") Long userId);
}
