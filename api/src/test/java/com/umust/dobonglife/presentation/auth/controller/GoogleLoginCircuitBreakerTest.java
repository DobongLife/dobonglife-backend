package com.umust.dobonglife.presentation.auth.controller;

import com.umust.dobonglife.application.auth.service.GoogleLoginApplicationService;
import com.umust.dobonglife.global.common.error.exception.ServiceCallException;
import com.umust.dobonglife.global.port.auth.dto.AuthTokens;
import com.umust.dobonglife.global.port.auth.dto.SocialAuthUserInfo;
import com.umust.dobonglife.global.port.auth.out.GoogleOAuthPort;
import com.umust.dobonglife.global.port.user.dto.OAuthLoginUser;
import com.umust.dobonglife.global.port.user.in.ManageUserUseCase;
import com.umust.dobonglife.global.port.user.in.OAuthFindUserUseCase;
import com.umust.dobonglife.global.port.auth.in.LoginSuccessUseCase;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Google 로그인 — 서킷 브레이커 시나리오")
class GoogleLoginCircuitBreakerTest {

    @InjectMocks
    GoogleLoginApplicationService googleLoginService;

    @Mock
    GoogleOAuthPort googleOAuthPort;

    @Mock
    OAuthFindUserUseCase oAuthFindUserUseCase;

    @Mock
    ManageUserUseCase manageUserUseCase;

    @Mock
    LoginSuccessUseCase loginSuccessUseCase;

    @Nested
    @DisplayName("ACT 1: 정상 상태 (CLOSED)")
    class NormalState {

        @Test
        @DisplayName("Google OAuth 정상 응답 시 로그인 성공")
        void 정상_로그인_성공() {
            SocialAuthUserInfo socialUser = new SocialAuthUserInfo("google-123", "test@gmail.com", "테스터");
            OAuthLoginUser loginUser = new OAuthLoginUser(1L, "테스터", Role.MEMBER);
            AuthTokens tokens = new AuthTokens("access-token", "refresh-token", "ROLE_USER");

            when(googleOAuthPort.verify("valid-id-token")).thenReturn(socialUser);
            when(oAuthFindUserUseCase.findOrCreateOAuthUser(
                    eq(Provider.GOOGLE), eq("google-123"), eq("test@gmail.com"), eq("테스터")
            )).thenReturn(loginUser);
            when(loginSuccessUseCase.issueLoginToken(1L, Provider.GOOGLE, Role.MEMBER, "테스터"))
                    .thenReturn(tokens);

            AuthTokens result = googleLoginService.login("valid-id-token", "fcm-token");

            assertThat(result.accessToken()).isEqualTo("access-token");
            verify(manageUserUseCase).updateFcmToken(1L, "fcm-token");
        }
    }

    @Nested
    @DisplayName("ACT 2: Google 서버 장애 (Timeout/500)")
    class GoogleServerFailure {

        @Test
        @DisplayName("auth-service 타임아웃 시 ServiceCallException 발생")
        void 타임아웃_시_ServiceCallException() {
            when(googleOAuthPort.verify("expired-token"))
                    .thenThrow(new ServiceCallException("auth-service", 0,
                            new RuntimeException("Read timed out")));

            assertThatThrownBy(() -> googleLoginService.login("expired-token", "fcm"))
                    .isInstanceOf(ServiceCallException.class)
                    .hasMessageContaining("서비스에 일시적으로 연결할 수 없습니다");

            verify(oAuthFindUserUseCase, never()).findOrCreateOAuthUser(any(), any(), any(), any());
            verify(loginSuccessUseCase, never()).issueLoginToken(anyLong(), any(), any(), any());
        }

        @Test
        @DisplayName("auth-service 500 응답 시 ServiceCallException 발생")
        void 서버에러_시_ServiceCallException() {
            when(googleOAuthPort.verify("any-token"))
                    .thenThrow(new ServiceCallException("auth-service", 500,
                            new RuntimeException("auth-service 응답: 500")));

            assertThatThrownBy(() -> googleLoginService.login("any-token", null))
                    .isInstanceOf(ServiceCallException.class)
                    .extracting(e -> ((ServiceCallException) e).getStatusCode())
                    .isEqualTo(500);

            verify(manageUserUseCase, never()).updateFcmToken(anyLong(), any());
        }
    }

    @Nested
    @DisplayName("ACT 3: 부분 실패 — FCM 업데이트 실패해도 로그인은 성공해야 하는지 검증")
    class PartialFailure {

        @Test
        @DisplayName("FCM 토큰 없으면 updateFcmToken 호출하지 않음")
        void fcm_null이면_업데이트_생략() {
            SocialAuthUserInfo socialUser = new SocialAuthUserInfo("google-456", "test2@gmail.com", "유저2");
            OAuthLoginUser loginUser = new OAuthLoginUser(2L, "유저2", Role.MEMBER);
            AuthTokens tokens = new AuthTokens("at", "rt", "ROLE_USER");

            when(googleOAuthPort.verify("token")).thenReturn(socialUser);
            when(oAuthFindUserUseCase.findOrCreateOAuthUser(any(), any(), any(), any())).thenReturn(loginUser);
            when(loginSuccessUseCase.issueLoginToken(anyLong(), any(), any(), any())).thenReturn(tokens);

            AuthTokens result = googleLoginService.login("token", null);

            assertThat(result).isNotNull();
            verify(manageUserUseCase, never()).updateFcmToken(anyLong(), any());
        }
    }
}
