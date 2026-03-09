package com.umust.dobonglife.domain.user.application.service;

import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.domain.user.dto.request.MailCodeCheckRequest;
import com.umust.dobonglife.domain.user.dto.request.MailRequest;
import com.umust.dobonglife.domain.user.application.port.MailSender;
import com.umust.dobonglife.domain.user.domain.UserRepository;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.domain.user.application.port.VerificationCodeStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class MailService {

    private static final long VERIFICATION_CODE_EXPIRY_MINUTES = 5;

    private static final long VERIFIED_TTL_SECONDS = 1800; // 30분

    private static final String EMAIL_KEY_PREFIX = "auth:email:";

    private static final String PASSWORD_KEY_PREFIX = "auth:password:";

    private final MailSender mailSender;
    private final VerificationCodeStore verificationCodeStore;
    private final UserRepository userRepository;

    public void sendMail(MailRequest request) {
        log.info("email={}, isForSignUp={}", request.getEmail(), request.isForSignUp());

        // 이미 존재하는 이메일이면, 메일 전송 취소
        if(request.isForSignUp()) {
            if(userRepository.existsByEmail(request.getEmail())) {
              throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
            }
        }

        // 비밀번호 변경 시, 존재하지 않는 email이면 에러 처리
        if (!request.isForSignUp()){
            userRepository.findByEmailAndProvider(request.getEmail(), Provider.LOCAL)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_MAIL_NOT_FOUND));
        }

        String authCode = createCode();
        String htmlContent = mailSender.renderTemplate("AuthCode-email.html", "code", authCode);

        String prefix;
        if(request.isForSignUp()) prefix = EMAIL_KEY_PREFIX;
        else prefix = PASSWORD_KEY_PREFIX;

        mailSender.send(request.getEmail(), "[도봉라이프] 이메일 인증을 위한 인증 코드 발송", htmlContent);
        String key = prefix + request.getEmail();
        verificationCodeStore.store(key, authCode, Duration.ofMinutes(VERIFICATION_CODE_EXPIRY_MINUTES));
    }

    public void checkAuthCode(MailCodeCheckRequest request) {

        String email = request.getEmail();
        String storedCode;
        String prefix;
        if(request.isForSignUp()){
            storedCode = getStoredSignUpCode(email);
            prefix = EMAIL_KEY_PREFIX;
        } else {
            storedCode = getStoredPasswordCode(email);
            prefix = PASSWORD_KEY_PREFIX;
        }

        if (storedCode == null || storedCode.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_CODE);
        }

        if ("VERIFIED".equals(storedCode)) {
            return; // 이미 인증이 완료된 이메일
        }

        if (!request.getAuthCode().equals(storedCode)) {
            log.info("Request Code: {}", request.getAuthCode());
            log.info("Stored code: {}", storedCode);
            throw new BusinessException(ErrorCode.INVALID_EMAIL_CODE);
        }

        verificationCodeStore.store(
                prefix + email,
                "VERIFIED",
                Duration.ofSeconds(VERIFIED_TTL_SECONDS)
        );
    }

    public void checkPasswordAuthCode(String email, String authCode) {
        String storedCode = getStoredPasswordCode(email);

        if (storedCode == null || storedCode.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_CODE);
        }

        if ("VERIFIED".equals(storedCode)) {
            return;
        }

        if (!authCode.equals(storedCode)) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_CODE);
        }
    }

    // 숫자 6자리로 인증 번호 구현하는 메서드
    public String createCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            key.append(random.nextInt(10));
        }

        return key.toString();
    }

    public String getStoredSignUpCode(String email) {
        String key = EMAIL_KEY_PREFIX + email;
        return verificationCodeStore.find(key).orElse(null);
    }

    public String getStoredPasswordCode(String email) {
        String key = PASSWORD_KEY_PREFIX + email;
        return verificationCodeStore.find(key).orElse(null);
    }

}
