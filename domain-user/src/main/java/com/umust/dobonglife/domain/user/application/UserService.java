package com.umust.dobonglife.domain.user.application;

import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.domain.user.exception.UserErrorCode;
import com.umust.dobonglife.domain.user.exception.UserException;
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

    @Transactional(readOnly = true)
    public void validateCouponExchange(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        if (user.isBlocked()) {
            throw new UserException(UserErrorCode.USER_BLOCKED);
        }
    }

    @Transactional(readOnly = true)
    public String getFcmToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        return user.getFcmToken();
    }
}
