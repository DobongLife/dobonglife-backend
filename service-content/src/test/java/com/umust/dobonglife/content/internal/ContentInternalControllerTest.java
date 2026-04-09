package com.umust.dobonglife.content.internal;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.like.application.port.in.LikeCleanupUseCase;
import com.umust.dobonglife.domain.like.application.port.in.LikeRestoreUseCase;
import com.umust.dobonglife.domain.review.application.port.in.ReviewCleanupUseCase;
import com.umust.dobonglife.domain.review.application.port.in.ReviewRestoreUseCase;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ContentInternalController.class, excludeAutoConfiguration = {
        OAuth2ClientAutoConfiguration.class,
        OAuth2ClientWebSecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class})
@Import(TestWebMvcConfig.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class ContentInternalControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean ReviewCleanupUseCase reviewCleanupUseCase;
    @MockitoBean ReviewRestoreUseCase reviewRestoreUseCase;
    @MockitoBean LikeCleanupUseCase likeCleanupUseCase;
    @MockitoBean LikeRestoreUseCase likeRestoreUseCase;
    @MockitoBean AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;
    @MockitoBean TokenExtractor tokenExtractor;

    @Test
    @DisplayName("리뷰 정리(cleanup) - 성공")
    void cleanupReviews_success() throws Exception {
        mockMvc.perform(post("/internal/withdraw/reviews/{userId}/cleanup", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-reviews-cleanup",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 API - 회원 탈퇴").summary("리뷰 정리")
                                .description("회원 탈퇴 시 리뷰를 정리(PENDING 처리)합니다.")
                                .pathParameters(parameterWithName("userId").description("사용자 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 없음"))
                                .build())
                ));

        verify(reviewCleanupUseCase).markPendingByUserId(1L);
    }

    @Test
    @DisplayName("좋아요 정리(cleanup) - 성공")
    void cleanupLikes_success() throws Exception {
        mockMvc.perform(post("/internal/withdraw/likes/{userId}/cleanup", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-likes-cleanup",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 API - 회원 탈퇴").summary("좋아요 정리")
                                .description("회원 탈퇴 시 좋아요를 정리(PENDING 처리)합니다.")
                                .pathParameters(parameterWithName("userId").description("사용자 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 없음"))
                                .build())
                ));

        verify(likeCleanupUseCase).markPendingByUserId(1L);
    }

    @Test
    @DisplayName("리뷰 복원(restore) - 성공")
    void restoreReviews_success() throws Exception {
        mockMvc.perform(post("/internal/withdraw/reviews/{userId}/restore", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-reviews-restore",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 API - 회원 탈퇴").summary("리뷰 복원")
                                .description("회원 탈퇴 철회 시 리뷰를 복원합니다.")
                                .pathParameters(parameterWithName("userId").description("사용자 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 없음"))
                                .build())
                ));

        verify(reviewRestoreUseCase).restoreByUserId(1L);
    }

    @Test
    @DisplayName("좋아요 복원(restore) - 성공")
    void restoreLikes_success() throws Exception {
        mockMvc.perform(post("/internal/withdraw/likes/{userId}/restore", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-likes-restore",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 API - 회원 탈퇴").summary("좋아요 복원")
                                .description("회원 탈퇴 철회 시 좋아요를 복원합니다.")
                                .pathParameters(parameterWithName("userId").description("사용자 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 없음"))
                                .build())
                ));

        verify(likeRestoreUseCase).restoreByUserId(1L);
    }

    @Test
    @DisplayName("리뷰 최종 삭제(finalize) - 성공")
    void finalizeReviews_success() throws Exception {
        mockMvc.perform(post("/internal/withdraw/reviews/{userId}/finalize", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-reviews-finalize",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 API - 회원 탈퇴").summary("리뷰 최종 삭제")
                                .description("회원 탈퇴 확정 시 리뷰를 최종 삭제합니다.")
                                .pathParameters(parameterWithName("userId").description("사용자 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 없음"))
                                .build())
                ));

        verify(reviewCleanupUseCase).finalizeByUserId(1L);
    }

    @Test
    @DisplayName("좋아요 최종 삭제(finalize) - 성공")
    void finalizeLikes_success() throws Exception {
        mockMvc.perform(post("/internal/withdraw/likes/{userId}/finalize", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-likes-finalize",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 API - 회원 탈퇴").summary("좋아요 최종 삭제")
                                .description("회원 탈퇴 확정 시 좋아요를 최종 삭제합니다.")
                                .pathParameters(parameterWithName("userId").description("사용자 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 없음"))
                                .build())
                ));

        verify(likeCleanupUseCase).finalizeByUserId(1L);
    }
}
