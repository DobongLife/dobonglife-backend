package com.umust.dobonglife.domain.user.service;

import com.umust.dobonglife.domain.user.controller.dto.request.MailCodeCheckRequest;
import com.umust.dobonglife.domain.user.controller.dto.request.MailRequest;
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
import java.util.Random;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class MailService {

    private static final long VERIFICATION_CODE_EXPIRY_MINUTES = 5;

    private static final long VERIFIED_TTL_SECONDS = 1800; // 30분

    private static final String EMAIL_KEY_PREFIX = "auth:email:";

    private final JavaMailSender javaMailSender;

    private final SpringTemplateEngine templateEngine;

    private final UserRepository userRepository;

    private final RedisService redisService;

    public void sendMail(MailRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL);
        }

        String authCode = createCode();
        MimeMessage mimeMessage = createEmailMessage(request.getEmail(), authCode);

        try {
            javaMailSender.send(mimeMessage);

            String key = EMAIL_KEY_PREFIX + request.getEmail();
            redisService.setValues(key, authCode, Duration.ofMinutes(VERIFICATION_CODE_EXPIRY_MINUTES));
        } catch (MailException e) {  //JavaMailSender의 전송과정에서 오류 발생 시
            throw new BusinessException(ErrorCode.MAIL_SEND_FAILED);
        }
    }

    public String sendPasswordMail(MailRequest request) {
        String password = createNewPassword();
        MimeMessage mimeMessage = createPasswordEmailMessage(request.getEmail(), password);
        try {
            javaMailSender.send(mimeMessage);
        } catch (MailException e) {  //JavaMailSender의 전송과정에서 오류 발생 시
            throw new BusinessException(ErrorCode.MAIL_SEND_FAILED);
        }
        return password;
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

    public void checkAuthCode(MailCodeCheckRequest request) {
        String email = request.getEmail();
        String storedCode = getStoredCode(email);

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

    public String getStoredCode(String email) {
        String key = EMAIL_KEY_PREFIX + email;
        return redisService.getValues(key);
    }

    // thymeleaf를 통한 html 적용
    public String setContext(String authCode) {
        Context context = new Context();
        context.setVariable("code", authCode);
        return templateEngine.process("AuthCode-email.html", context);
    }

    // thymeleaf를 통한 html 적용
    public String setPasswordContext(String password) {
        Context context = new Context();
        context.setVariable("password", password);
        return templateEngine.process("Password-email.html", context);
    }

    // 임시 비밀번호를 구현하는 메서드
    public String createNewPassword() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0: key.append((char) ((int) random.nextInt(26) + 97)); break;
                case 1: key.append((char) ((int) random.nextInt(26) + 65)); break;
                default: key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }

    private MimeMessage createPasswordEmailMessage(String recipient, String password) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            mimeMessageHelper.setTo(recipient);
            mimeMessageHelper.setSubject("[도봉라이프] 임시 비밀번호 발송");
            mimeMessageHelper.setText(setPasswordContext(password), true);

            return mimeMessage;
        } catch (MessagingException e) {  // SMTP 전송 오류, 포맷 오류 발생 시
            throw new BusinessException(ErrorCode.MAIL_SEND_FAILED);
        }
    }
}

