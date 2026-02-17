package com.umust.dobonglife.domain.user.service;

import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.auth.exception.handler.CustomAuthenticationEntryPoint;
import com.umust.dobonglife.domain.user.controller.dto.request.MailCodeCheckRequest;
import com.umust.dobonglife.domain.user.controller.dto.request.MailRequest;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.external.redis.RedisService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @InjectMocks
    private MailService mailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private RedisService redisService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("sendMail - 회원가입용 메일 전송 성공")
    void sendMail_회원가입_성공() {
        // given
        MailRequest request = new MailRequest("new@example.com", true);
        given(userRepository.existsByEmail("new@example.com")).willReturn(false);

        MimeMessage mimeMessage = mock(MimeMessage.class);
        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
        given(templateEngine.process(anyString(), any())).willReturn("<html>123456</html>");

        // when
        mailService.sendMail(request);

        // then
        verify(javaMailSender).send(any(MimeMessage.class));
        verify(redisService).setValues(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("sendMail - 회원가입 시 중복 이메일이면 USER_DUPLICATE_EMAIL 예외")
    void sendMail_회원가입_중복이메일_예외() {
        // given
        MailRequest request = new MailRequest("dup@example.com", true);
        given(userRepository.existsByEmail("dup@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> mailService.sendMail(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("sendMail - 비밀번호 변경용 메일 전송 성공")
    void sendMail_비밀번호변경_성공() {
        // given
        MailRequest request = new MailRequest("existing@example.com", false);
        User user = User.builder().id(1L).email("existing@example.com").provider(Provider.LOCAL).build();
        given(userRepository.findByEmailAndProvider("existing@example.com", Provider.LOCAL))
                .willReturn(Optional.of(user));

        MimeMessage mimeMessage = mock(MimeMessage.class);
        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);
        given(templateEngine.process(anyString(), any())).willReturn("<html>123456</html>");

        // when
        mailService.sendMail(request);

        // then
        verify(javaMailSender).send(any(MimeMessage.class));
        verify(redisService).setValues(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("sendMail - 비밀번호 변경 시 이메일 없으면 USER_MAIL_NOT_FOUND 예외")
    void sendMail_비밀번호변경_이메일없음_예외() {
        // given
        MailRequest request = new MailRequest("notfound@example.com", false);
        given(userRepository.findByEmailAndProvider("notfound@example.com", Provider.LOCAL))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> mailService.sendMail(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_MAIL_NOT_FOUND);
    }

    @Test
    @DisplayName("checkAuthCode - 회원가입 인증 코드 검증 성공")
    void checkAuthCode_회원가입_성공() {
        // given
        MailCodeCheckRequest request = new MailCodeCheckRequest("test@example.com", "123456", true);
        given(redisService.getValues("auth:email:test@example.com")).willReturn("123456");

        // when
        mailService.checkAuthCode(request);

        // then
        verify(redisService).setValues(
                org.mockito.ArgumentMatchers.eq("auth:email:test@example.com"),
                org.mockito.ArgumentMatchers.eq("VERIFIED"),
                any()
        );
    }

    @Test
    @DisplayName("checkAuthCode - 코드 불일치 시 INVALID_EMAIL_CODE 예외")
    void checkAuthCode_코드불일치_예외() {
        // given
        MailCodeCheckRequest request = new MailCodeCheckRequest("test@example.com", "wrong", true);
        given(redisService.getValues("auth:email:test@example.com")).willReturn("123456");

        // when & then
        assertThatThrownBy(() -> mailService.checkAuthCode(request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_EMAIL_CODE);
    }

    @Test
    @DisplayName("checkAuthCode - 이미 인증된 상태면 바로 리턴")
    void checkAuthCode_이미인증됨_성공() {
        // given
        MailCodeCheckRequest request = new MailCodeCheckRequest("test@example.com", "123456", true);
        given(redisService.getValues("auth:email:test@example.com")).willReturn("VERIFIED");

        // when - 예외 없이 정상 실행
        mailService.checkAuthCode(request);
    }

    @Test
    @DisplayName("checkPasswordAuthCode - 비밀번호용 인증 코드 검증 성공")
    void checkPasswordAuthCode_성공() {
        // given
        given(redisService.getValues("auth:password:test@example.com")).willReturn("654321");

        // when - 예외 없이 정상 실행
        mailService.checkPasswordAuthCode("test@example.com", "654321");
    }

    @Test
    @DisplayName("createCode - 6자리 숫자 코드 생성")
    void createCode_6자리() {
        // when
        String code = mailService.createCode();

        // then
        assertThat(code).hasSize(6);
        assertThat(code).matches("\\d{6}");
    }
}
