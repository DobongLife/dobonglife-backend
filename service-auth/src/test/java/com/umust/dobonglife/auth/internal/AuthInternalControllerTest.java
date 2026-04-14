package com.umust.dobonglife.auth.internal;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.umust.dobonglife.auth.client.UserServiceClient;
import com.umust.dobonglife.auth.client.UserServiceClient.UserProviderResponse;
import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.domain.auth.application.port.in.AuthTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.auth.application.port.in.RevokeSocialAccountUseCase;
import com.umust.dobonglife.global.common.constant.Provider;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AuthInternalController.class, excludeAutoConfiguration = {OAuth2ClientAutoConfiguration.class, OAuth2ClientWebSecurityAutoConfiguration.class, SecurityAutoConfiguration.class})
@Import(TestWebMvcConfig.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class AuthInternalControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean RevokeSocialAccountUseCase revokeSocialAccountUseCase;
    @MockitoBean AuthTokenUseCase authTokenUseCase;
    @MockitoBean UserServiceClient userServiceClient;
    @MockitoBean TokenExtractor tokenExtractor;
    @MockitoBean AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;

    @Test
    @DisplayName("소셜 계정 연동 해제 - 성공")
    void revokeSocialAccount_success() throws Exception {
        given(userServiceClient.getUserProvider(1L))
                .willReturn(new UserProviderResponse("KAKAO", "kakao-provider-id"));
        willDoNothing().given(revokeSocialAccountUseCase).revoke(eq(Provider.KAKAO), eq("kakao-provider-id"));

        mockMvc.perform(post("/internal/auth/{userId}/revoke-social", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-auth-revoke-social",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 인증 API").summary("소셜 계정 연동 해제")
                                .description("사용자의 소셜 계정 연동을 해제합니다.")
                                .pathParameters(parameterWithName("userId").description("사용자 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(userServiceClient).getUserProvider(eq(1L));
        verify(revokeSocialAccountUseCase).revoke(eq(Provider.KAKAO), eq("kakao-provider-id"));
    }

    @Test
    @DisplayName("토큰 무효화 - accessToken과 refreshToken 모두 전달")
    void invalidateToken_withBothTokens() throws Exception {
        willDoNothing().given(authTokenUseCase).invalidateAccessToken(anyString());
        willDoNothing().given(authTokenUseCase).deleteRefreshToken(anyString());

        mockMvc.perform(post("/internal/auth/invalidate-token").with(csrf())
                        .param("accessToken", "access-token-value")
                        .param("refreshToken", "refresh-token-value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-auth-invalidate-token",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 인증 API").summary("토큰 무효화")
                                .description("Access Token을 블랙리스트에 추가하고 Refresh Token을 삭제합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(authTokenUseCase).invalidateAccessToken(eq("access-token-value"));
        verify(authTokenUseCase).deleteRefreshToken(eq("refresh-token-value"));
    }

    @Test
    @DisplayName("토큰 무효화 - accessToken만 전달")
    void invalidateToken_withAccessTokenOnly() throws Exception {
        willDoNothing().given(authTokenUseCase).invalidateAccessToken(anyString());

        mockMvc.perform(post("/internal/auth/invalidate-token").with(csrf())
                        .param("accessToken", "access-token-value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print());

        verify(authTokenUseCase).invalidateAccessToken(eq("access-token-value"));
        verify(authTokenUseCase, never()).deleteRefreshToken(anyString());
    }
}
