package com.umust.dobonglife.domain.user.domain.repository;

import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.vo.UserIsBlocked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
    SELECT UserIsBlocked(u.isBlocked) FROM User u 
    """)
    UserIsBlocked findUserIsBlocked(Long userId);
}
