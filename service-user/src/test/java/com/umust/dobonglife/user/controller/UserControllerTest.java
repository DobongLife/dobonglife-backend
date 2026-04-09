package com.umust.dobonglife.user.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.user.application.port.in.CheckAuthCodeUseCase;
import com.umust.dobonglife.domain.user.application.port.in.SendMailUseCase;
import com.umust.dobonglife.domain.user.application.port.in.SignUpUseCase;
import com.umust.dobonglife.domain.user.application.port.in.UpdatePasswordUseCase;
import com.umust.dobonglife.test.support.TestWebMvcConfig;
import com.umust.dobonglife.test.support.WithMockCustomUser;
import com.umust.dobonglife.user.withdraw.WithdrawOrchestrator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = {
        OAuth2ClientAutoConfiguration.class,
        OAuth2ClientWebSecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class
})
@Import(TestWebMvcConfig.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean SignUpUseCase signUpUseCase;
    @MockitoBean UpdatePasswordUseCase updatePasswordUseCase;
    @MockitoBean SendMailUseCase sendMailUseCase;
    @MockitoBean CheckAuthCodeUseCase checkAuthCodeUseCase;
    @MockitoBean WithdrawOrchestrator withdrawOrchestrator;
    @MockitoBean TokenExtractor tokenExtractor;
    @MockitoBean AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;

    @Test
    @DisplayName("회원가입 - 성공")
    void signUp_success() throws Exception {
        willDoNothing().given(signUpUseCase).signUp(anyString(), anyString(), anyString());

        mockMvc.perform(post("/api/users/signup").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "test@example.com", "name": "김도봉", "password": "password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(print())
                .andDo(document("user-signup",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("사용자 API").summary("회원가입")
                                .description("이메일, 이름, 비밀번호로 회원가입합니다.")
                                .requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(signUpUseCase).signUp("test@example.com", "김도봉", "password123");
    }

    @Test
    @DisplayName("인증코드 전송 - 성공")
    void sendAuthCodeMail_success() throws Exception {
        willDoNothing().given(sendMailUseCase).sendMail(anyString(), anyBoolean());

        mockMvc.perform(post("/api/users/mail/send").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "test@example.com", "isForSignUp": true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("user-mail-send",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("사용자 API").summary("인증코드 전송")
                                .description("이메일로 인증코드를 전송합니다.")
                                .requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("isForSignUp").type(JsonFieldType.BOOLEAN).description("회원가입용 여부"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(sendMailUseCase).sendMail("test@example.com", true);
    }

    @Test
    @DisplayName("인증코드 확인 - 성공")
    void checkAuthCode_success() throws Exception {
        willDoNothing().given(checkAuthCodeUseCase).checkAuthCode(anyString(), anyString(), anyBoolean());

        mockMvc.perform(post("/api/users/mail/check").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "test@example.com", "authCode": "123456", "isForSignUp": true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("user-mail-check",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("사용자 API").summary("인증코드 확인")
                                .description("이메일로 전송된 인증코드를 확인합니다.")
                                .requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("authCode").type(JsonFieldType.STRING).description("인증코드"),
                                        fieldWithPath("isForSignUp").type(JsonFieldType.BOOLEAN).description("회원가입용 여부"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(checkAuthCodeUseCase).checkAuthCode("test@example.com", "123456", true);
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공")
    void updatePassword_success() throws Exception {
        willDoNothing().given(updatePasswordUseCase).updateMyPassword(anyString(), anyString(), anyString());

        mockMvc.perform(patch("/api/users/password").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "test@example.com", "authCode": "123456", "newPassword": "newPassword123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("user-password-update",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("사용자 API").summary("비밀번호 변경")
                                .description("인증코드 확인 후 비밀번호를 변경합니다.")
                                .requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("authCode").type(JsonFieldType.STRING).description("인증코드"),
                                        fieldWithPath("newPassword").type(JsonFieldType.STRING).description("새 비밀번호"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(updatePasswordUseCase).updateMyPassword("test@example.com", "123456", "newPassword123");
    }

    @Test
    @DisplayName("회원 탈퇴 - 성공")
    @WithMockCustomUser
    void deleteAccount_success() throws Exception {
        willDoNothing().given(withdrawOrchestrator).execute(any(), any(), any());

        mockMvc.perform(post("/api/users/delete/account").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("user-delete-account",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("사용자 API").summary("회원 탈퇴")
                                .description("현재 사용자의 계정을 삭제합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(withdrawOrchestrator).execute(eq(1L), any(), any());
    }
}
