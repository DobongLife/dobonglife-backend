package com.umust.dobonglife.domain.user.service;

import com.umust.dobonglife.domain.auth.exception.CustomAuthenticationException;
import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.auth.service.JwtService;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.domain.point.domain.repository.PointRepository;
import com.umust.dobonglife.domain.user.controller.dto.request.SignupRequest;
import com.umust.dobonglife.domain.user.controller.dto.response.MyPageResponse;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.common.response.ErrorCode;
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

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtService jwtService;
    private final PointRepository pointRepository;

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
                .role(Role.MEMBER)
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

    @Transactional
    public MyPageResponse getMyPage(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        int eventCount = eventRepository.countByUserId(userId);
        int couponCount = couponRepository.countByUserId(userId);
        int reviewCount = reviewRepository.countByUserId(userId);
        int totalEarnedPoint = pointRepository.sumEarnedPoint(userId);

        return new MyPageResponse(
                new MyPageResponse.Profile(
                        user.getName(),
                        user.getEmail(),
                        formatJoinedAt(user.getCreatedAt()),
                        user.getBalance()
                ),
                new MyPageResponse.Summary(
                        eventCount,
                        couponCount,
                        reviewCount,
                        totalEarnedPoint
                )
        );
    }

    private String formatJoinedAt(LocalDateTime createdAt) {
        return createdAt.getYear() + "년 " + createdAt.getMonthValue() + "월";
    }
}

