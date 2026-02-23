package com.umust.dobonglife.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.domain.auth.controller.dto.response.TokenResponse;
import com.umust.dobonglife.domain.auth.service.AppleAuthService;
import com.umust.dobonglife.domain.auth.service.AuthService;
import com.umust.dobonglife.domain.auth.service.GoogleAuthService;
import com.umust.dobonglife.domain.auth.service.JwtService;
import com.umust.dobonglife.domain.auth.service.KakaoAuthService;
import com.umust.dobonglife.global.external.firebase.FirebaseConfig;
import com.umust.dobonglife.global.support.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    KakaoAuthService kakaoAuthService;

    @MockitoBean
    GoogleAuthService googleAuthService;

    @MockitoBean
    AppleAuthService appleAuthService;

    @MockitoBean
    AuthService authService;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Test
    @DisplayName("카카오 로그인 - 성공")
    @WithMockCustomUser
    void kakaoLogin_success() throws Exception {
        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken("access-token-value")
                .refreshToken("refresh-token-value")
                .role("ROLE_MEMBER")
                .build();

        given(kakaoAuthService.login(any())).willReturn(tokenResponse);

        String requestJson = """
                {
                    "accessToken": "kakao-access-token",
                    "fcmToken": "firebase-token"
                }
                """;

        mockMvc.perform(post("/api/auth/login/kakao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token-value"))
                .andDo(print())
                .andDo(document("auth-login-kakao",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("인증 인가 API")
                                        .summary("카카오 로그인")
                                        .description("카카오 Access Token으로 로그인합니다.")
                                        .requestFields(
                                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("카카오 Access Token"),
                                                fieldWithPath("fcmToken").type(JsonFieldType.STRING).description("FCM 토큰")
                                        )
                                        .responseFields(
                                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("Access Token"),
                                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("Refresh Token"),
                                                fieldWithPath("data.role").type(JsonFieldType.STRING).description("사용자 역할")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("구글 로그인 - 성공")
    @WithMockCustomUser
    void googleLogin_success() throws Exception {
        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken("access-token-value")
                .refreshToken("refresh-token-value")
                .role("ROLE_MEMBER")
                .build();

        given(googleAuthService.login(any())).willReturn(tokenResponse);

        String requestJson = """
                {
                    "idToken": "google-id-token",
                    "fcmToken": "firebase-token"
                }
                """;

        mockMvc.perform(post("/api/auth/login/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token-value"))
                .andDo(print())
                .andDo(document("auth-login-google",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("인증 인가 API")
                                        .summary("구글 로그인")
                                        .description("구글 ID Token으로 로그인합니다.")
                                        .requestFields(
                                                fieldWithPath("idToken").type(JsonFieldType.STRING).description("구글 ID Token"),
                                                fieldWithPath("fcmToken").type(JsonFieldType.STRING).description("FCM 토큰")
                                        )
                                        .responseFields(
                                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("Access Token"),
                                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("Refresh Token"),
                                                fieldWithPath("data.role").type(JsonFieldType.STRING).description("사용자 역할")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("애플 로그인 - 성공")
    @WithMockCustomUser
    void appleLogin_success() throws Exception {
        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken("access-token-value")
                .refreshToken("refresh-token-value")
                .role("ROLE_MEMBER")
                .build();

        given(appleAuthService.login(any())).willReturn(tokenResponse);

        String requestJson = """
                {
                    "identityToken": "apple-identity-token",
                    "fcmToken": "firebase-token",
                    "name": "홍길동"
                }
                """;

        mockMvc.perform(post("/api/auth/login/apple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token-value"))
                .andDo(print())
                .andDo(document("auth-login-apple",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("인증 인가 API")
                                        .summary("애플 로그인")
                                        .description("Apple Identity Token으로 로그인합니다.")
                                        .requestFields(
                                                fieldWithPath("identityToken").type(JsonFieldType.STRING).description("Apple Identity Token"),
                                                fieldWithPath("fcmToken").type(JsonFieldType.STRING).description("FCM 토큰"),
                                                fieldWithPath("name").type(JsonFieldType.STRING).description("사용자 이름 (최초 로그인 시)")
                                        )
                                        .responseFields(
                                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("Access Token"),
                                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("Refresh Token"),
                                                fieldWithPath("data.role").type(JsonFieldType.STRING).description("사용자 역할")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("로그아웃 - 성공")
    @WithMockCustomUser
    void logout_success() throws Exception {
        willDoNothing().given(authService).logout(any());

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("auth-logout",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("인증 인가 API")
                                        .summary("로그아웃")
                                        .description("현재 사용자를 로그아웃합니다.")
                                        .responseFields(
                                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)")
                                        )
                                        .build()
                        )
                ));
    }

    @Test
    @DisplayName("토큰 재발급 - 성공")
    @WithMockCustomUser
    void reissue_success() throws Exception {
        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .role("ROLE_MEMBER")
                .build();

        given(jwtService.reissueTokens(any(), any())).willReturn(tokenResponse);

        mockMvc.perform(post("/api/auth/reissue")
                        .header("Authorization-refresh", "Bearer refresh-token-value")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                .andDo(print())
                .andDo(document("auth-reissue",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("인증 인가 API")
                                        .summary("토큰 재발급")
                                        .description("Refresh Token으로 Access Token을 재발급합니다.")
                                        .responseFields(
                                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("재발급된 Access Token"),
                                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("재발급된 Refresh Token"),
                                                fieldWithPath("data.role").type(JsonFieldType.STRING).description("사용자 역할")
                                        )
                                        .build()
                        )
                ));
    }
}
