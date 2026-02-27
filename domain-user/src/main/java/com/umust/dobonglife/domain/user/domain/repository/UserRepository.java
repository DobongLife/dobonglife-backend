package com.umust.dobonglife.domain.user.domain.repository;

import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.vo.UserIsBlocked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
    SELECT new com.umust.dobonglife.domain.user.domain.vo.UserIsBlocked(u.isBlocked)
    FROM User u
    WHERE u.id = :userId
    """)
    UserIsBlocked findUserIsBlocked(@Param("userId") Long userId);
}
