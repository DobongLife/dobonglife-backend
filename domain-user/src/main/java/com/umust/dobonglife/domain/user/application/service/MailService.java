package com.umust.dobonglife.domain.user.application.service;

import com.umust.dobonglife.domain.user.application.port.in.CheckAuthCodeUseCase;
import com.umust.dobonglife.domain.user.application.port.in.SendMailUseCase;
import com.umust.dobonglife.domain.user.application.port.out.LoadUserPort;
import com.umust.dobonglife.domain.user.exception.UserErrorCode;
import com.umust.dobonglife.domain.user.exception.UserException;
import com.umust.dobonglife.global.port.user.out.MailSender;
import com.umust.dobonglife.global.port.user.out.VerificationCodeStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MailService implements SendMailUseCase, CheckAuthCodeUseCase {

    private static final long VERIFICATION_CODE_EXPIRY_MINUTES = 5;
    private static final long VERIFIED_TTL_SECONDS = 1800;
    private static final String EMAIL_KEY_PREFIX = "auth:email:";
    private static final String PASSWORD_KEY_PREFIX = "auth:password:";

    private final MailSender mailSender;
    private final VerificationCodeStore verificationCodeStore;
    private final LoadUserPort loadUserPort;

    // ── SendMailUseCase ──

    @Override
    public void sendMail(String email, boolean forSignUp) {
        log.info("email={}, isForSignUp={}", email, forSignUp);

        if (forSignUp) {
            if (loadUserPort.existsByEmail(email)) {
                throw new UserException(UserErrorCode.USER_EMAIL_ALREADY_EXISTS);
            }
        }

        if (!forSignUp) {
            loadUserPort.loadLocalUser(email);
        }

        String authCode = createCode();
        String htmlContent = mailSender.renderTemplate("AuthCode-email.html", "code", authCode);
        String prefix = forSignUp ? EMAIL_KEY_PREFIX : PASSWORD_KEY_PREFIX;

        mailSender.send(email, "[도봉라이프] 이메일 인증을 위한 인증 코드 발송", htmlContent);
        verificationCodeStore.store(prefix + email, authCode, Duration.ofMinutes(VERIFICATION_CODE_EXPIRY_MINUTES));
    }

    // ── CheckAuthCodeUseCase ──

    @Override
    public void checkAuthCode(String email, String authCode, boolean forSignUp) {
        String storedCode;
        String prefix;
        if (forSignUp) {
            storedCode = getStoredSignUpCode(email);
            prefix = EMAIL_KEY_PREFIX;
        } else {
            storedCode = getStoredPasswordCode(email);
            prefix = PASSWORD_KEY_PREFIX;
        }

        if (storedCode == null || storedCode.isBlank()) {
            throw new UserException(UserErrorCode.INVALID_EMAIL_CODE);
        }

        if ("VERIFIED".equals(storedCode)) {
            return;
        }

        if (!authCode.equals(storedCode)) {
            log.info("Request Code: {}", authCode);
            log.info("Stored code: {}", storedCode);
            throw new UserException(UserErrorCode.INVALID_EMAIL_CODE);
        }

        verificationCodeStore.store(prefix + email, "VERIFIED", Duration.ofSeconds(VERIFIED_TTL_SECONDS));
    }

    @Override
    public void checkPasswordAuthCode(String email, String authCode) {
        String storedCode = getStoredPasswordCode(email);

        if (storedCode == null || storedCode.isBlank()) {
            throw new UserException(UserErrorCode.INVALID_EMAIL_CODE);
        }

        if ("VERIFIED".equals(storedCode)) {
            return;
        }

        if (!authCode.equals(storedCode)) {
            throw new UserException(UserErrorCode.INVALID_EMAIL_CODE);
        }
    }

    @Override
    public String getStoredSignUpCode(String email) {
        return verificationCodeStore.find(EMAIL_KEY_PREFIX + email).orElse(null);
    }

    @Override
    public String getStoredPasswordCode(String email) {
        return verificationCodeStore.find(PASSWORD_KEY_PREFIX + email).orElse(null);
    }

    private String createCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            key.append(random.nextInt(10));
        }
        return key.toString();
    }
}
