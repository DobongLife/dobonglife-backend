package com.umust.dobonglife.domain.user.application;

import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public boolean isUserBlocked(Long userId) {
        return userRepository.isUserBlocked(userId);
    }
}
