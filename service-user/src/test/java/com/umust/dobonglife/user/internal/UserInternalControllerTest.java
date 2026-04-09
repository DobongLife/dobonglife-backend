package com.umust.dobonglife.user.internal;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.user.application.dto.OAuthLoginUser;
import com.umust.dobonglife.domain.user.application.port.in.DeleteAccountUseCase;
import com.umust.dobonglife.domain.user.application.port.in.GetUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.OAuthFindUserUseCase;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import com.umust.dobonglife.test.support.TestWebMvcConfig;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserInternalController.class, excludeAutoConfiguration = {
        OAuth2ClientAutoConfiguration.class,
        OAuth2ClientWebSecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class
})
@Import(TestWebMvcConfig.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class UserInternalControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean DeleteAccountUseCase deleteAccountUseCase;
    @MockitoBean GetUserUseCase getUserUseCase;
    @MockitoBean ManageUserUseCase manageUserUseCase;
    @MockitoBean OAuthFindUserUseCase oAuthFindUserUseCase;
    @MockitoBean TokenExtractor tokenExtractor;
    @MockitoBean AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;

    // ── Withdraw saga endpoints ──

    @Test
    @DisplayName("탈퇴 대기 상태로 변경 - 성공")
    void markPending_success() throws Exception {
        willDoNothing().given(deleteAccountUseCase).markPending(anyLong());

        mockMvc.perform(post("/internal/users/{userId}/mark-pending", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-user-mark-pending",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 사용자 API").summary("탈퇴 대기 상태 변경")
                                .description("사용자를 탈퇴 대기 상태로 변경합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(deleteAccountUseCase).markPending(1L);
    }

    @Test
    @DisplayName("계정 삭제 - 성공")
    void deleteAccount_success() throws Exception {
        willDoNothing().given(deleteAccountUseCase).deleteAccount(anyLong());

        mockMvc.perform(post("/internal/users/{userId}/delete", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-user-delete",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 사용자 API").summary("계정 삭제")
                                .description("사용자 계정을 삭제합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(deleteAccountUseCase).deleteAccount(1L);
    }

    @Test
    @DisplayName("계정 복원 - 성공")
    void restoreAccount_success() throws Exception {
        willDoNothing().given(deleteAccountUseCase).restoreAccount(anyLong());

        mockMvc.perform(post("/internal/users/{userId}/restore", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-user-restore",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 사용자 API").summary("계정 복원")
                                .description("삭제 대기 중인 사용자 계정을 복원합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(deleteAccountUseCase).restoreAccount(1L);
    }

    // ── User query endpoints ──

    @Test
    @DisplayName("사용자 프로바이더 조회 - 성공")
    void getUserProvider_success() throws Exception {
        given(getUserUseCase.getProvider(1L)).willReturn(Provider.LOCAL);
        given(getUserUseCase.getProviderId(1L)).willReturn("local-id-123");

        mockMvc.perform(get("/internal/users/{userId}/provider", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.provider").value("LOCAL"))
                .andExpect(jsonPath("$.data.providerId").value("local-id-123"))
                .andDo(print())
                .andDo(document("internal-user-provider",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 사용자 API").summary("프로바이더 조회")
                                .description("사용자의 인증 프로바이더 정보를 조회합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("프로바이더 정보"),
                                        fieldWithPath("data.provider").type(JsonFieldType.STRING).description("프로바이더"),
                                        fieldWithPath("data.providerId").type(JsonFieldType.STRING).description("프로바이더 ID"))
                                .build())
                ));

        verify(getUserUseCase).getProvider(1L);
        verify(getUserUseCase).getProviderId(1L);
    }

    @Test
    @DisplayName("활성 사용자 여부 조회 - 성공")
    void isActiveUser_success() throws Exception {
        given(getUserUseCase.isNotActiveUser(1L)).willReturn(false);

        mockMvc.perform(get("/internal/users/{userId}/active", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true))
                .andDo(print())
                .andDo(document("internal-user-active",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 사용자 API").summary("활성 사용자 여부 조회")
                                .description("사용자가 활성 상태인지 확인합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.BOOLEAN).description("활성 여부"))
                                .build())
                ));

        verify(getUserUseCase).isNotActiveUser(1L);
    }

    @Test
    @DisplayName("차단 사용자 여부 조회 - 성공")
    void isBlockedUser_success() throws Exception {
        given(getUserUseCase.isBlockedUser(1L)).willReturn(false);

        mockMvc.perform(get("/internal/users/{userId}/blocked", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false))
                .andDo(print())
                .andDo(document("internal-user-blocked",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 사용자 API").summary("차단 사용자 여부 조회")
                                .description("사용자가 차단 상태인지 확인합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.BOOLEAN).description("차단 여부"))
                                .build())
                ));

        verify(getUserUseCase).isBlockedUser(1L);
    }

    @Test
    @DisplayName("FCM 토큰 조회 - 성공")
    void getFcmToken_success() throws Exception {
        given(getUserUseCase.getFcmToken(1L)).willReturn("fcm-token-abc123");

        mockMvc.perform(get("/internal/users/{userId}/fcm-token", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("fcm-token-abc123"))
                .andDo(print())
                .andDo(document("internal-user-fcm-token-get",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 사용자 API").summary("FCM 토큰 조회")
                                .description("사용자의 FCM 토큰을 조회합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.STRING).description("FCM 토큰"))
                                .build())
                ));

        verify(getUserUseCase).getFcmToken(1L);
    }

    @Test
    @DisplayName("FCM 토큰 무효화 - 성공")
    void invalidateFcmToken_success() throws Exception {
        willDoNothing().given(manageUserUseCase).inValidFcmToken(anyLong());

        mockMvc.perform(post("/internal/users/{userId}/fcm-token/invalidate", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-user-fcm-token-invalidate",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 사용자 API").summary("FCM 토큰 무효화")
                                .description("사용자의 FCM 토큰을 무효화합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(manageUserUseCase).inValidFcmToken(1L);
    }

    @Test
    @DisplayName("FCM 토큰 업데이트 - 성공")
    void updateFcmToken_success() throws Exception {
        willDoNothing().given(manageUserUseCase).updateFcmToken(anyLong(), anyString());

        mockMvc.perform(post("/internal/users/{userId}/fcm-token", 1L).with(csrf())
                        .param("fcmToken", "new-fcm-token-xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-user-fcm-token-update",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 사용자 API").summary("FCM 토큰 업데이트")
                                .description("사용자의 FCM 토큰을 업데이트합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(manageUserUseCase).updateFcmToken(1L, "new-fcm-token-xyz");
    }

    @Test
    @DisplayName("쿠폰 교환 가능 여부 확인 - 성공")
    void canExchangeCoupon_success() throws Exception {
        willDoNothing().given(manageUserUseCase).canExchangeCoupon(anyLong());

        mockMvc.perform(post("/internal/users/{userId}/can-exchange", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-user-can-exchange",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 사용자 API").summary("쿠폰 교환 가능 여부 확인")
                                .description("사용자가 쿠폰 교환 가능한지 확인합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(manageUserUseCase).canExchangeCoupon(1L);
    }

    @Test
    @DisplayName("OAuth 사용자 조회 또는 생성 - 성공")
    void findOrCreateOAuthUser_success() throws Exception {
        OAuthLoginUser oAuthLoginUser = new OAuthLoginUser(100L, "김도봉", Role.MEMBER);
        given(oAuthFindUserUseCase.findOrCreateOAuthUser(any(Provider.class), anyString(), anyString(), anyString()))
                .willReturn(oAuthLoginUser);

        mockMvc.perform(post("/internal/users/oauth/find-or-create").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "provider": "KAKAO",
                                    "providerId": "kakao-12345",
                                    "email": "oauth@example.com",
                                    "name": "김도봉"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(100))
                .andExpect(jsonPath("$.data.role").value("MEMBER"))
                .andExpect(jsonPath("$.data.name").value("김도봉"))
                .andDo(print())
                .andDo(document("internal-user-oauth-find-or-create",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 사용자 API").summary("OAuth 사용자 조회/생성")
                                .description("OAuth 사용자를 조회하거나 새로 생성합니다.")
                                .requestFields(
                                        fieldWithPath("provider").type(JsonFieldType.STRING).description("OAuth 프로바이더"),
                                        fieldWithPath("providerId").type(JsonFieldType.STRING).description("프로바이더 사용자 ID"),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("이름"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("OAuth 사용자 정보"),
                                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                        fieldWithPath("data.role").type(JsonFieldType.STRING).description("사용자 역할"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("사용자 이름"))
                                .build())
                ));

        verify(oAuthFindUserUseCase).findOrCreateOAuthUser(Provider.KAKAO, "kakao-12345", "oauth@example.com", "김도봉");
    }
}
