package com.umust.dobonglife.domain.user.infrastructure;

import com.umust.dobonglife.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
