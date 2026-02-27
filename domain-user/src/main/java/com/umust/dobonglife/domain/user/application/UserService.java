package com.umust.dobonglife.domain.user.application;

import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean isUserBlocked(Long userId) {
        return userRepository.isUserBlocked(userId);
    }
}
