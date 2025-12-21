package com.umust.dobonglife.domain.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.domain.auth.dto.request.RefreshTokenRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

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


        // 1) 필터가 request에서 토큰을 뽑아가는 단계 (프로젝트에 이 메서드가 있다면)
        when(jwtUtil.extractAccessToken(any(HttpServletRequest.class)))
                .thenReturn(Optional.of(TEST_ACCESS_TOKEN));

        // when & then
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
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Access Token (Bearer {token})")
                        ),
                        requestFields(
                                fieldWithPath("refreshToken").description("Refresh Token")
                        )
                ));
        verify(jwtService).logout(any(HttpServletRequest.class),
                argThat(r -> TEST_REFRESH_TOKEN.equals(r.getRefreshToken())));
    }
}
