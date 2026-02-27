package com.umust.dobonglife.domain.user.application;

import com.umust.dobonglife.domain.user.application.dto.request.UserIsBlockedRequest;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.domain.user.domain.vo.UserIsBlocked;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public UserIsBlocked getUserIsBlocked(UserIsBlockedRequest request){
        Long userId = request.userId();
        return userRepository.findUserIsBlocked(userId);
    }
}
