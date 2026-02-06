package com.umust.dobonglife.domain.user.service;

import com.umust.dobonglife.domain.auth.exception.CustomAuthenticationException;
import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.auth.service.JwtService;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.domain.user.controller.dto.request.PasswordUpdateRequest;
import com.umust.dobonglife.domain.user.controller.dto.request.SignupRequest;
import com.umust.dobonglife.domain.user.controller.dto.response.MyPageResponse;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.error.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final MailService mailService;

    @Transactional
    public void signUp(SignupRequest request) {
        if (userRepository.findByEmailAndProvider(request.getEmail(), Provider.LOCAL).isPresent()) {
            throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
        }
        if (!"VERIFIED".equals(mailService.getStoredSignUpCode(request.getEmail()))){
            throw new BusinessException(ErrorCode.AUTHCODE_UNAUTHORIZED);
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
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        inValidFcmToken(userId);
        userRepository.delete(user);
        SecurityContextHolder.clearContext();
    }

    @Transactional
    public void updateMyPassword(PasswordUpdateRequest request) {
        User user = userRepository.findByEmailAndProvider(request.getEmail(), Provider.LOCAL)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if(user.getPassword() == null){
            throw new BusinessException(ErrorCode.USER_IS_SOCIAL_LOGGED);
        }
        mailService.checkPasswordAuthCode(request.getEmail(), request.getAuthCode());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }

    public boolean validateOwner(Long userId, Long ownerId) {
        if (!userId.equals(ownerId)) {
            return false;
        }
        return true;
    }

    @Transactional
    public User findOrCreateOAuthUser(Provider provider, String providerUserId, String email, String name) {
        return userRepository.findByProviderAndProviderId(provider, providerUserId)
                .orElseGet(() -> createOAuthUserSafely(provider, providerUserId, email, name));
    }

    private User createOAuthUserSafely(Provider provider, String providerId, String email, String name) {
        try {
            User user = User.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .email(email)
                    .name(name != null ? name : "이름 없는 사용자")
                    .role(Role.MEMBER)
                    .build();

            return userRepository.save(user);

        } catch (DataIntegrityViolationException e) {
            // 동시 로그인 등으로 이미 생성된 경우(유니크 충돌) 재조회해서 반환
            return userRepository.findByProviderAndProviderId(provider, providerId)
                    .orElseThrow(() -> e);
        }
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 User 엔티티가 존재하지 않습니다: " + userId));
    }

    public MyPageResponse getUserInfo(Long userId){
        User byId = findById(userId);
        return new MyPageResponse(byId.getName(), byId.getEmail(), byId.getRole());
    }

    public Long getUserTotalPoint(Long userId) {
        User byId = findById(userId);
        return byId.getBalance();
    }

    public Role getUserRole(Long userId) {
        User byId = findById(userId);
        return byId.getRole();
    }

    @Transactional
    public void handleDeletion(Long userId) {
        User byId = findById(userId);
        byId.handleDeletion();
        userRepository.save(byId);
    }

    @Transactional
    public void canExchangeCoupon(Long userId) {
        User byId = findById(userId);
        if(!byId.canExchangeCoupon())
            throw new BusinessException(ErrorCode.COUPON_EXCHANGE_RESTRICTED);

    }

    public boolean isBlockedUser(Long userId) {
        User byId = findById(userId);
        return byId.isBlocked();
    }

    public boolean isReceivedAlarmUser(Long userId) {
        User byId = findById(userId);
        return byId.isReceivedAlarm();
    }

    @Transactional
    public void updateNotificationSetting(Long userId, boolean enabled) {
        User byId = findById(userId);
        byId.updateNotificationEnabled(enabled);
    }

    @Transactional
    public void updateFcmToken(Long userId, String fcmToken) {
        User byId = findById(userId);
        byId.setFcmToken(fcmToken);
    }

    public void inValidFcmToken(Long userId) {
        User byId = findById(userId);
        byId.setFcmToken(null);
    }
}

