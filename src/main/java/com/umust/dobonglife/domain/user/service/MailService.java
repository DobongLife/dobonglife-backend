package com.umust.dobonglife.domain.user.service;

import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.user.controller.dto.request.MailCodeCheckRequest;
import com.umust.dobonglife.domain.user.controller.dto.request.MailRequest;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.external.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

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

    private final JavaMailSender javaMailSender;

    private final SpringTemplateEngine templateEngine;

    private final RedisService redisService;
    private final UserRepository userRepository;

    public void sendMail(MailRequest request) {
        log.info("email={}, isForSignUp={}", request.getEmail(), request.isForSignUp());

        // 비밀번호 변경 시, 존재하지 않는 email이면 에러 처리
        if (!request.isForSignUp()){
            User user = userRepository.findByEmailAndProvider(request.getEmail(), Provider.LOCAL)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_MAIL_NOT_FOUND));
        }

        String authCode = createCode();
        MimeMessage mimeMessage = createEmailMessage(request.getEmail(), authCode);

        String prefix;
        if(request.isForSignUp()) prefix = EMAIL_KEY_PREFIX;
        else prefix = PASSWORD_KEY_PREFIX;
        try {
            javaMailSender.send(mimeMessage);
            String key = prefix + request.getEmail();
            redisService.setValues(key, authCode, Duration.ofMinutes(VERIFICATION_CODE_EXPIRY_MINUTES));
        } catch (MailException e) {  //JavaMailSender의 전송과정에서 오류 발생 시
            throw new BusinessException(ErrorCode.MAIL_SEND_FAILED);
        }
    }

    private MimeMessage createEmailMessage(String recipient, String authCode) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            mimeMessageHelper.setTo(recipient);
            mimeMessageHelper.setSubject("[도봉라이프] 이메일 인증을 위한 인증 코드 발송");
            mimeMessageHelper.setText(setContext(authCode), true);

            return mimeMessage;
        } catch (MessagingException e) {  // SMTP 전송 오류, 포맷 오류 발생 시
            throw new BusinessException(ErrorCode.MAIL_SEND_FAILED);
        }
    }

    public void checkSignUpAuthCode(MailCodeCheckRequest request) {

        String email = request.getEmail();
        String storedCode = getStoredSignUpCode(email);

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

        redisService.setValues(
                EMAIL_KEY_PREFIX + email,
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

        redisService.setValues(
                PASSWORD_KEY_PREFIX + email,
                "VERIFIED",
                Duration.ofSeconds(VERIFIED_TTL_SECONDS)
        );
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
        return redisService.getValues(key);
    }

    public String getStoredPasswordCode(String email) {
        String key = PASSWORD_KEY_PREFIX + email;
        return redisService.getValues(key);
    }

    // thymeleaf를 통한 html 적용
    public String setContext(String authCode) {
        Context context = new Context();
        context.setVariable("code", authCode);
        return templateEngine.process("AuthCode-email.html", context);
    }
}

