package com.umust.dobonglife.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.domain.auth.service.AuthService;
import com.umust.dobonglife.domain.user.controller.dto.request.MailCodeCheckRequest;
import com.umust.dobonglife.domain.user.controller.dto.request.MailRequest;
import com.umust.dobonglife.domain.user.controller.dto.request.PasswordUpdateRequest;
import com.umust.dobonglife.domain.user.controller.dto.request.SignupRequest;
import com.umust.dobonglife.domain.user.service.MailService;
import com.umust.dobonglife.domain.user.service.UserService;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private MailService mailService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    // =========================================================================
    // POST /api/users/signup - 회원가입
    // =========================================================================

    @Test
    @WithMockCustomUser
    @DisplayName("회원가입 - 성공")
    void 회원가입_성공() throws Exception {
        // given
        SignupRequest request = new SignupRequest("dobonglife@gmail.com", "김도봉", "password123");
        willDoNothing().given(userService).signUp(any(SignupRequest.class));

        // when & then
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(print())
                .andDo(document("user-signup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("사용자 API")
                                .summary("회원가입")
                                .description("이메일로 회원가입합니다.")
                                .requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING)
                                                .description("이메일"),
                                        fieldWithPath("name").type(JsonFieldType.STRING)
                                                .description("이름"),
                                        fieldWithPath("password").type(JsonFieldType.STRING)
                                                .description("비밀번호")
                                )
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                                .description("성공 여부"),
                                        fieldWithPath("status").type(JsonFieldType.NUMBER)
                                                .description("상태 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL)
                                                .description("응답 데이터")
                                )
                                .build()
                        )
                ));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("회원가입 - 중복 이메일")
    void 회원가입_중복이메일() throws Exception {
        // given
        SignupRequest request = new SignupRequest("dup@gmail.com", "김도봉", "password123");
        willThrow(new BusinessException(ErrorCode.USER_DUPLICATE_EMAIL))
                .given(userService).signUp(any(SignupRequest.class));

        // when & then
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print())
                .andDo(document("user-signup-duplicate-email",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("사용자 API")
                                .summary("회원가입 실패 - 중복 이메일")
                                .description("중복 이메일로 회원가입 시 에러를 반환합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                                .description("성공 여부"),
                                        fieldWithPath("status").type(JsonFieldType.NUMBER)
                                                .description("에러 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("에러 메시지"),
                                        fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                .description("에러 발생 시각")
                                )
                                .build()
                        )
                ));
    }

    // =========================================================================
    // POST /api/users/mail/send - 인증코드 전송
    // =========================================================================

    @Test
    @WithMockCustomUser
    @DisplayName("인증코드 전송 - 성공")
    void 인증코드전송_성공() throws Exception {
        // given
        MailRequest request = new MailRequest("dobonglife@gmail.com", true);
        willDoNothing().given(mailService).sendMail(any(MailRequest.class));

        // when & then
        mockMvc.perform(post("/api/users/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andDo(print())
                .andDo(document("user-mail-send",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("사용자 API")
                                .summary("인증코드 전송")
                                .description("이메일로 인증코드를 전송합니다.")
                                .requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING)
                                                .description("이메일"),
                                        fieldWithPath("forSignUp").type(JsonFieldType.BOOLEAN)
                                                .description("회원가입 여부 (true: 회원가입, false: 비밀번호 변경)")
                                )
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                                .description("성공 여부"),
                                        fieldWithPath("status").type(JsonFieldType.NUMBER)
                                                .description("상태 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL)
                                                .description("응답 데이터")
                                )
                                .build()
                        )
                ));
    }

    // =========================================================================
    // POST /api/users/mail/check - 인증코드 확인
    // =========================================================================

    @Test
    @WithMockCustomUser
    @DisplayName("인증코드 확인 - 성공")
    void 인증코드확인_성공() throws Exception {
        // given
        MailCodeCheckRequest request = new MailCodeCheckRequest("dobonglife@gmail.com", "123456", true);
        willDoNothing().given(mailService).checkAuthCode(any(MailCodeCheckRequest.class));

        // when & then
        mockMvc.perform(post("/api/users/mail/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andDo(print())
                .andDo(document("user-mail-check",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("사용자 API")
                                .summary("인증코드 확인")
                                .description("이메일 인증코드를 확인합니다.")
                                .requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING)
                                                .description("이메일"),
                                        fieldWithPath("authCode").type(JsonFieldType.STRING)
                                                .description("인증 코드 6자리"),
                                        fieldWithPath("forSignUp").type(JsonFieldType.BOOLEAN)
                                                .description("회원가입 여부 (true: 회원가입, false: 비밀번호 변경)")
                                )
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                                .description("성공 여부"),
                                        fieldWithPath("status").type(JsonFieldType.NUMBER)
                                                .description("상태 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL)
                                                .description("응답 데이터")
                                )
                                .build()
                        )
                ));
    }

    // =========================================================================
    // PATCH /api/users/password - 비밀번호 변경
    // =========================================================================

    @Test
    @WithMockCustomUser
    @DisplayName("비밀번호 변경 - 성공")
    void 비밀번호변경_성공() throws Exception {
        // given
        PasswordUpdateRequest request = new PasswordUpdateRequest("dobonglife@gmail.com", "123456", "newPassword");
        willDoNothing().given(userService).updateMyPassword(any(PasswordUpdateRequest.class));

        // when & then
        mockMvc.perform(patch("/api/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andDo(print())
                .andDo(document("user-password-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("사용자 API")
                                .summary("비밀번호 변경")
                                .description("비밀번호를 변경합니다.")
                                .requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING)
                                                .description("이메일"),
                                        fieldWithPath("authCode").type(JsonFieldType.STRING)
                                                .description("인증 코드"),
                                        fieldWithPath("newPassword").type(JsonFieldType.STRING)
                                                .description("새 비밀번호")
                                )
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                                .description("성공 여부"),
                                        fieldWithPath("status").type(JsonFieldType.NUMBER)
                                                .description("상태 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL)
                                                .description("응답 데이터")
                                )
                                .build()
                        )
                ));
    }

    // =========================================================================
    // POST /api/users/delete/account - 회원탈퇴
    // =========================================================================

    @Test
    @WithMockCustomUser
    @DisplayName("회원탈퇴 - 성공")
    void 회원탈퇴_성공() throws Exception {
        // given
        willDoNothing().given(authService).deleteAccount(any(), any());

        // when & then
        mockMvc.perform(post("/api/users/delete/account")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andDo(print())
                .andDo(document("user-delete-account",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("사용자 API")
                                .summary("회원 탈퇴")
                                .description("회원 탈퇴를 진행합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                                .description("성공 여부"),
                                        fieldWithPath("status").type(JsonFieldType.NUMBER)
                                                .description("상태 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL)
                                                .description("응답 데이터")
                                )
                                .build()
                        )
                ));
    }
}
