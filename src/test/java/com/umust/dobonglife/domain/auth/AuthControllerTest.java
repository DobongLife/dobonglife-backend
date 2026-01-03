//package com.umust.dobonglife.domain.auth;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import com.umust.dobonglife.domain.auth.controller.dto.request.FormLoginRequest;
//import com.umust.dobonglife.domain.auth.domain.constant.Provider;
//import com.umust.dobonglife.domain.auth.domain.entity.UserPrincipal;
//import com.umust.dobonglife.domain.auth.service.CustomUserDetailsService;
//import com.umust.dobonglife.domain.auth.service.JwtService;
//import com.umust.dobonglife.domain.auth.utils.JwtUtil;
//import com.umust.dobonglife.domain.user.domain.constant.Role;
//import com.umust.dobonglife.domain.user.domain.entity.User;
//import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
//import com.umust.dobonglife.global.support.WithMockCustomUser;
//import jakarta.servlet.http.HttpServletRequest;
//import com.umust.dobonglife.domain.user.controller.dto.request.SignupRequest;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
//import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
//import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
//import static org.springframework.restdocs.payload.PayloadDocumentation.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
//
//
//@AutoConfigureRestDocs
//@ActiveProfiles("test")
//@SpringBootTest
//@AutoConfigureMockMvc
//class AuthControllerTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//    @MockitoBean
//    CustomUserDetailsService customUserDetailsService;
//
//    @Autowired
//    UserRepository userRepository;
//
//    private static final String SIGNUP_URL = "/api/users/signup";
//    private static final String TEST_EMAIL = "test@example.com";
//    private static final String TEST_PASSWORD = "1234";
//    private static final String LOGIN_URL = "/api/auth/login";
//
//    @Test
//    @DisplayName("회원가입 성공 - DB에 실제로 저장된다")
//    void signup_success_persists_user() throws Exception {
//        // given
//        SignupRequest request = new SignupRequest(TEST_EMAIL, "test", TEST_PASSWORD);
//        String requestJson = objectMapper.writeValueAsString(request);
//
//        // when & then
//        mockMvc.perform(post(SIGNUP_URL)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(requestJson))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.status").value(200))
//                .andDo(document(
//                        "user-signup",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        requestFields(
//                                fieldWithPath("email").description("사용자 이메일"),
//                                fieldWithPath("name").description("사용자 이름"),
//                                fieldWithPath("password").description("비밀번호")
//                        ),
//                        responseFields(
//                                fieldWithPath("success").description("성공 여부"),
//                                fieldWithPath("status").description("상태 코드"),
//                                fieldWithPath("message").description("메시지"),
//                                fieldWithPath("data").description("응답 데이터")
//                        )
//                ));
//
//        // 진짜 저장되었는지 DB 검증
//        Optional<User> saved = userRepository.findByEmailAndProvider(TEST_EMAIL, Provider.LOCAL);
//        assertThat(saved).isPresent();
//        assertThat(saved.get().getEmail()).isEqualTo(TEST_EMAIL);
//
//        // 비밀번호는 보통 인코딩돼서 저장되므로 "원문과 다르다" 정도만 확인하는 게 일반적
//        assertThat(saved.get().getPassword()).isNotBlank();
//        assertThat(saved.get().getPassword()).isNotEqualTo(TEST_PASSWORD);
//    }
//
//    @Test
//    @DisplayName("로그인 필터를 이용한 로그인")
//    void customFormLogin_Success_200() throws Exception {
//        // given
//        FormLoginRequest loginRequest = new FormLoginRequest(TEST_EMAIL, TEST_PASSWORD);
//        String requestJson = objectMapper.writeValueAsString(loginRequest);
//        String encodedPassword = passwordEncoder.encode(TEST_PASSWORD);
//
//        UserPrincipal userPrincipal = UserPrincipal.builder()
//                .userId(1L)
//                .userName(TEST_EMAIL)
//                .password(encodedPassword)
//                .role(Role.MEMBER)
//                .provider(Provider.LOCAL)
//                .authorities(List.of(new SimpleGrantedAuthority(Role.MEMBER.getRole())))
//                .build();
//
//        // when & then
//        when(customUserDetailsService.loadUserByUsername(TEST_EMAIL))
//                .thenReturn(userPrincipal);
//        mockMvc.perform(post(LOGIN_URL)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestJson))
//                .andExpect(status().isOk())
//                .andDo(document(
//                        "auth-login",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        requestFields(
//                                fieldWithPath("email").description("사용자 이메일"),
//                                fieldWithPath("password").description("사용자 비밀번호")
//                        ),
//                        responseFields(
//                                fieldWithPath("success").description("성공 여부"),
//                                fieldWithPath("status").description("상태 코드"),
//                                fieldWithPath("message").description("응답 메시지"),
//
//                                fieldWithPath("data").description("토큰 정보"),
//                                fieldWithPath("data.accessToken").description("Access Token (Bearer 인증에 사용)"),
//                                fieldWithPath("data.refreshToken").description("Refresh Token (재발급/로그아웃에 사용)")
//                        )
//                ));
//    }
//}