package com.umust.dobonglife.domain.auth;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.domain.auth.service.CustomUserDetailsService;
import com.umust.dobonglife.domain.auth.service.JwtService;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.global.support.WithMockCustomUser;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.umust.dobonglife.domain.auth.controller.dto.request.RefreshTokenRequest;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;

import java.util.Optional;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureRestDocs
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AuthLogoutTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    JwtUtil jwtUtil;

    private static final String LOGOUT_URL = "/api/auth/logout";

    @Test
    @DisplayName("로그아웃 성공 - RefreshToken 무효화")
    @WithMockCustomUser
    void logout_User() throws Exception {
        // given
        String TEST_ACCESS_TOKEN = "access-token";
        String TEST_REFRESH_TOKEN = "refresh-token";
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(TEST_REFRESH_TOKEN);
        String json = objectMapper.writeValueAsString(refreshTokenRequest);

        // when & then
        when(jwtUtil.extractAccessToken(any(HttpServletRequest.class)))
                .thenReturn(Optional.of(TEST_ACCESS_TOKEN));
        mockMvc.perform(post(LOGOUT_URL)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(document(
                        "auth-logout",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증 인가 API")
                                .summary("로그아웃")
                                .description("RefreshToken을 무효화하여 로그아웃 처리합니다.")
                                .requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION)
                                                .description("Bearer {accessToken}")
                                )
                                .requestFields(
                                        fieldWithPath("refreshToken")
                                                .type(JsonFieldType.STRING)
                                                .description("무효화할 Refresh Token")
                                )
                                // (선택) 응답 필드까지 있으면 더 예쁨
                                // .responseFields(...)
                                .build()),

                        // ✅ REST Docs 스니펫도 계속 만들고 싶으면 같이 둬도 됨
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("인증된 사용자의 Access Token (Bearer {accessToken} 형식)")
                        ),
                        requestFields(
                                fieldWithPath("refreshToken")
                                        .type(JsonFieldType.STRING)
                                        .description("로그아웃 처리 대상 Refresh Token")
                        )
                ));
        verify(jwtService).logout(any(HttpServletRequest.class),
                argThat(r -> TEST_REFRESH_TOKEN.equals(r.getRefreshToken())));
    }
}
