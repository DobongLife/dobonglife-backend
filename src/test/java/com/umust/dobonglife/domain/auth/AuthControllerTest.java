package com.umust.dobonglife.domain.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.domain.auth.dto.request.FormLoginRequest;
import com.umust.dobonglife.domain.auth.dto.request.RefreshTokenRequest;
import com.umust.dobonglife.domain.auth.model.Provider;
import com.umust.dobonglife.domain.auth.model.UserPrincipal;
import com.umust.dobonglife.domain.auth.service.CustomUserDetailsService;
import com.umust.dobonglife.domain.auth.service.JwtService;
import com.umust.dobonglife.domain.user.model.Role;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;


@AutoConfigureRestDocs
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

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

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String LOGIN_URL = "/api/auth/login";
    private static final String LOGOUT_URL = "/api/auth/logout";

    @Test
    @DisplayName("로그인 필터를 이용한 로그인")
    void customFormLogin_Success_302() throws Exception {
        // given
        FormLoginRequest loginRequest = new FormLoginRequest(TEST_EMAIL, TEST_PASSWORD);
        String requestJson = objectMapper.writeValueAsString(loginRequest);
        String encodedPassword = passwordEncoder.encode(TEST_PASSWORD);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId(1L)
                .userName(TEST_EMAIL)
                .password(encodedPassword)
                .role(Role.MEMBER)
                .provider(Provider.LOCAL)
                .authorities(List.of(new SimpleGrantedAuthority(Role.MEMBER.getRole())))
                .build();
        //when
        when(customUserDetailsService.loadUserByUsername(TEST_EMAIL))
                .thenReturn(userPrincipal);
        // when & then
        mockMvc.perform(post(LOGIN_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().is3xxRedirection())
                .andDo(document(
                        "auth-login",                              // 스니펫 이름 (폴더 이름)
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(                             // 요청 필드 명세
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("password").description("사용자 비밀번호")
                        )
                ));
    }

    @Test
    @DisplayName("로그아웃 성공 - RefreshToken 무효화")
    @WithMockCustomUser
    void logout_User() throws Exception {
        // given
        String TEST_ACCESS_TOKEN = "access-token";
        String TEST_REFRESH_TOKEN = "refresh-token";

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(TEST_REFRESH_TOKEN);
        String json = objectMapper.writeValueAsString(refreshTokenRequest);

        doNothing().when(jwtService)
                .logout(any(HttpServletRequest.class), any(RefreshTokenRequest.class));

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