package com.umust.dobonglife.auth.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.umust.dobonglife.auth.service.AppleLoginService;
import com.umust.dobonglife.auth.service.AuthFacade;
import com.umust.dobonglife.auth.service.GoogleLoginService;
import com.umust.dobonglife.auth.service.KakaoLoginService;
import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.domain.auth.application.dto.AuthTokens;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.test.support.TestWebMvcConfig;
import com.umust.dobonglife.test.support.WithMockCustomUser;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AuthController.class, excludeAutoConfiguration = {OAuth2ClientAutoConfiguration.class, OAuth2ClientWebSecurityAutoConfiguration.class, SecurityAutoConfiguration.class})
@Import(TestWebMvcConfig.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean KakaoLoginService kakaoLoginService;
    @MockitoBean GoogleLoginService googleLoginService;
    @MockitoBean AppleLoginService appleLoginService;
    @MockitoBean AuthFacade authFacade;
    @MockitoBean TokenExtractor tokenExtractor;
    @MockitoBean AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;

    private static ResourceSnippetParameters tokenResponseResource(String tag, String summary, String description,
                                                                     org.springframework.restdocs.payload.FieldDescriptor... extraRequestFields) {
        var builder = ResourceSnippetParameters.builder()
                .tag(tag).summary(summary).description(description)
                .responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("Access Token"),
                        fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("Refresh Token"));
        if (extraRequestFields.length > 0) builder.requestFields(extraRequestFields);
        return builder.build();
    }

    @Test
    @DisplayName("카카오 로그인 - 성공")
    void kakaoLogin_success() throws Exception {
        given(kakaoLoginService.login(anyString(), any()))
                .willReturn(new AuthTokens("access-token", "refresh-token", "ROLE_MEMBER"));

        mockMvc.perform(post("/api/auth/login/kakao").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"accessToken": "kakao-access-token", "fcmToken": "fcm-token"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andDo(print())
                .andDo(document("auth-login-kakao",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(tokenResponseResource("인증 API", "카카오 로그인", "카카오 Access Token으로 로그인합니다.",
                                        fieldWithPath("accessToken").type(JsonFieldType.STRING).description("카카오 Access Token"),
                                        fieldWithPath("fcmToken").type(JsonFieldType.STRING).description("FCM 토큰")))
                ));

        verify(kakaoLoginService).login(eq("kakao-access-token"), eq("fcm-token"));
    }

    @Test
    @DisplayName("구글 로그인 - 성공")
    void googleLogin_success() throws Exception {
        given(googleLoginService.login(anyString(), any()))
                .willReturn(new AuthTokens("access-token", "refresh-token", "ROLE_MEMBER"));

        mockMvc.perform(post("/api/auth/login/google").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idToken": "google-id-token", "fcmToken": "fcm-token"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andDo(print())
                .andDo(document("auth-login-google",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(tokenResponseResource("인증 API", "구글 로그인", "구글 ID Token으로 로그인합니다.",
                                        fieldWithPath("idToken").type(JsonFieldType.STRING).description("구글 ID Token"),
                                        fieldWithPath("fcmToken").type(JsonFieldType.STRING).description("FCM 토큰")))
                ));

        verify(googleLoginService).login(eq("google-id-token"), eq("fcm-token"));
    }

    @Test
    @DisplayName("애플 로그인 - 성공")
    void appleLogin_success() throws Exception {
        given(appleLoginService.login(anyString(), any(), any()))
                .willReturn(new AuthTokens("access-token", "refresh-token", "ROLE_MEMBER"));

        mockMvc.perform(post("/api/auth/login/apple").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"identityToken": "apple-identity-token", "fcmToken": "fcm-token", "providerToken": "provider-token"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andDo(print())
                .andDo(document("auth-login-apple",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(tokenResponseResource("인증 API", "애플 로그인", "Apple Identity Token으로 로그인합니다.",
                                        fieldWithPath("identityToken").type(JsonFieldType.STRING).description("Apple Identity Token"),
                                        fieldWithPath("fcmToken").type(JsonFieldType.STRING).description("FCM 토큰"),
                                        fieldWithPath("providerToken").type(JsonFieldType.STRING).description("Apple Provider Token")))
                ));

        verify(appleLoginService).login(eq("apple-identity-token"), eq("fcm-token"), eq("provider-token"));
    }

    @Test
    @DisplayName("로그아웃 - 성공")
    @WithMockCustomUser
    void logout_success() throws Exception {
        given(tokenExtractor.extractAccessToken(any())).willReturn("access-token");
        given(tokenExtractor.extractRefreshToken(any())).willReturn("refresh-token");
        willDoNothing().given(authFacade).logout(anyString(), anyString(), any());

        mockMvc.perform(post("/api/auth/logout").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("auth-logout",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("인증 API").summary("로그아웃")
                                .description("현재 사용자를 로그아웃합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(authFacade).logout(eq("access-token"), eq("refresh-token"), eq(1L));
    }

    @Test
    @DisplayName("토큰 재발급 - 성공")
    void reissue_success() throws Exception {
        given(tokenExtractor.extractRefreshToken(any())).willReturn("old-refresh-token");
        given(authFacade.reissueTokens(anyString()))
                .willReturn(new AuthTokens("new-access-token", "new-refresh-token", "ROLE_MEMBER"));

        mockMvc.perform(post("/api/auth/reissue").with(csrf())
                        .header("Authorization-refresh", "Bearer old-refresh-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                .andDo(print())
                .andDo(document("auth-reissue",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(tokenResponseResource("인증 API", "토큰 재발급", "Refresh Token으로 Access Token을 재발급합니다."))
                ));

        verify(tokenExtractor).extractRefreshToken(any());
        verify(authFacade).reissueTokens(eq("old-refresh-token"));
    }
}
