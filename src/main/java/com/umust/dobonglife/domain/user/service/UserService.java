package com.umust.dobonglife.domain.user.service;

import com.umust.dobonglife.domain.auth.exception.CustomAuthenticationException;
import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.auth.service.JwtService;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.domain.user.controller.dto.request.SignupRequest;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.error.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.umust.dobonglife.domain.user.domain.entity.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtService jwtService;

    @Transactional
    public void signUp(SignupRequest request) {
        if (!userRepository.findByEmailAndProvider(request.getEmail(), Provider.LOCAL).isEmpty()) {
            throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
        }
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .provider(Provider.LOCAL)
                .role(Role.fromValue(request.getRole()))
                .build();
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount (HttpServletRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
        String accessToken = jwtUtil.extractAccessToken(request)
                .orElseThrow(() -> new CustomAuthenticationException(ErrorCode.SECURITY_INVALID_ACCESS_TOKEN));
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                jwtService.invalidAccessToken(accessToken);
            }
        });
        SecurityContextHolder.clearContext();
    }

    public boolean isCourseRemoved(Long id1, Long id2) {
        return id1 == id2;
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 User 엔티티가 존재하지 않습니다: " + userId));
    }
}

