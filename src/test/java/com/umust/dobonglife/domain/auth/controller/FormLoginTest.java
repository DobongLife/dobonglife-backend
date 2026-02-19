package com.umust.dobonglife.domain.auth.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.auth.domain.entity.UserPrincipal;
import com.umust.dobonglife.domain.auth.service.CustomUserDetailsService;
import com.umust.dobonglife.domain.auth.service.JwtService;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.external.firebase.FirebaseConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class FormLoginTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    UserService userService;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    private UserPrincipal testUserPrincipal;

    @BeforeEach
    void setUp() {
        String encodedPassword = passwordEncoder.encode("password123");
        testUserPrincipal = UserPrincipal.builder()
                .userId(1L)
                .userName("test@test.com")
                .password(encodedPassword)
                .role(Role.MEMBER)
                .provider(Provider.LOCAL)
                .authorities(Collections.singleton(Role.MEMBER.toAuthority()))
                .build();
    }

    @Test
    @DisplayName("폼 로그인 - 성공")
    void formLogin_success() throws Exception {
        given(customUserDetailsService.loadUserByUsername("test@test.com"))
                .willReturn(testUserPrincipal);

        String requestJson = """
                {
                    "email": "test@test.com",
                    "password": "password123",
                    "fcmToken": "firebase-token-value"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.data.role").value("ROLE_MEMBER"))
                .andDo(print())
                .andDo(document("auth-login-form",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("인증 인가 API")
                                        .summary("폼 로그인")
                                        .description("이메일/비밀번호로 로그인합니다.")
                                        .requestFields(
                                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
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
    @DisplayName("폼 로그인 실패 - 잘못된 비밀번호")
    void formLogin_invalidPassword() throws Exception {
        given(customUserDetailsService.loadUserByUsername("test@test.com"))
                .willReturn(testUserPrincipal);

        String requestJson = """
                {
                    "email": "test@test.com",
                    "password": "wrongPassword",
                    "fcmToken": "firebase-token-value"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."))
                .andDo(print())
                .andDo(document("auth-login-form-fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("인증 인가 API")
                                        .summary("폼 로그인 실패")
                                        .description("잘못된 이메일 또는 비밀번호로 로그인을 시도합니다.")
                                        .requestFields(
                                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                                fieldWithPath("fcmToken").type(JsonFieldType.STRING).description("FCM 토큰")
                                        )
                                        .responseFields(
                                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("에러 코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("에러 발생 시간")
                                        )
                                        .build()
                        )
                ));
    }
}
