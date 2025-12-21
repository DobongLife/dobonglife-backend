package com.umust.dobonglife.domain.auth;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.umust.dobonglife.domain.auth.controller.dto.request.FormLoginRequest;
import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.auth.domain.entity.UserPrincipal;
import com.umust.dobonglife.domain.auth.service.CustomUserDetailsService;
import com.umust.dobonglife.domain.auth.service.JwtService;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.domain.user.domain.constant.Role;
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
import java.util.Optional;

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

    @MockitoBean
    JwtUtil jwtUtil;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String LOGIN_URL = "/api/auth/login";


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
                .andExpect(status().isOk())
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
}