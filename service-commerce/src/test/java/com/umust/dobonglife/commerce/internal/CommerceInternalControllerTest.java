package com.umust.dobonglife.commerce.internal;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.coupon.application.port.in.CouponCleanupUseCase;
import com.umust.dobonglife.domain.coupon.application.port.in.CouponRestoreUseCase;
import com.umust.dobonglife.domain.point.application.port.in.PointCleanupUseCase;
import com.umust.dobonglife.domain.point.application.port.in.PointRestoreUseCase;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CommerceInternalController.class, excludeAutoConfiguration = {OAuth2ClientAutoConfiguration.class, OAuth2ClientWebSecurityAutoConfiguration.class, SecurityAutoConfiguration.class})
@Import(TestWebMvcConfig.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class CommerceInternalControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean CouponCleanupUseCase couponCleanupUseCase;
    @MockitoBean CouponRestoreUseCase couponRestoreUseCase;
    @MockitoBean PointCleanupUseCase pointCleanupUseCase;
    @MockitoBean PointRestoreUseCase pointRestoreUseCase;
    @MockitoBean TokenExtractor tokenExtractor;
    @MockitoBean AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;

    @Test
    @DisplayName("쿠폰 정리(cleanup) - 성공")
    void cleanupCoupons_success() throws Exception {
        willDoNothing().given(couponCleanupUseCase).markPendingByUserId(1L);

        mockMvc.perform(post("/internal/withdraw/coupons/{userId}/cleanup", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-commerce-coupon-cleanup",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 커머스 API").summary("쿠폰 정리")
                                .description("탈퇴 처리를 위해 사용자의 쿠폰을 대기 상태로 마킹합니다.")
                                .pathParameters(parameterWithName("userId").description("사용자 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(couponCleanupUseCase).markPendingByUserId(1L);
    }

    @Test
    @DisplayName("포인트 정리(cleanup) - 성공")
    void cleanupPoints_success() throws Exception {
        willDoNothing().given(pointCleanupUseCase).markPendingByUserId(1L);

        mockMvc.perform(post("/internal/withdraw/points/{userId}/cleanup", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-commerce-point-cleanup",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 커머스 API").summary("포인트 정리")
                                .description("탈퇴 처리를 위해 사용자의 포인트를 대기 상태로 마킹합니다.")
                                .pathParameters(parameterWithName("userId").description("사용자 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(pointCleanupUseCase).markPendingByUserId(1L);
    }

    @Test
    @DisplayName("쿠폰 복원(restore) - 성공")
    void restoreCoupons_success() throws Exception {
        willDoNothing().given(couponRestoreUseCase).restoreByUserId(1L);

        mockMvc.perform(post("/internal/withdraw/coupons/{userId}/restore", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-commerce-coupon-restore",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 커머스 API").summary("쿠폰 복원")
                                .description("탈퇴 롤백 시 사용자의 쿠폰을 복원합니다.")
                                .pathParameters(parameterWithName("userId").description("사용자 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(couponRestoreUseCase).restoreByUserId(1L);
    }

    @Test
    @DisplayName("포인트 복원(restore) - 성공")
    void restorePoints_success() throws Exception {
        willDoNothing().given(pointRestoreUseCase).restoreByUserId(1L);

        mockMvc.perform(post("/internal/withdraw/points/{userId}/restore", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-commerce-point-restore",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 커머스 API").summary("포인트 복원")
                                .description("탈퇴 롤백 시 사용자의 포인트를 복원합니다.")
                                .pathParameters(parameterWithName("userId").description("사용자 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(pointRestoreUseCase).restoreByUserId(1L);
    }

    @Test
    @DisplayName("쿠폰 최종 삭제(finalize) - 성공")
    void finalizeCoupons_success() throws Exception {
        willDoNothing().given(couponCleanupUseCase).finalizeByUserId(1L);

        mockMvc.perform(post("/internal/withdraw/coupons/{userId}/finalize", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-commerce-coupon-finalize",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 커머스 API").summary("쿠폰 최종 삭제")
                                .description("탈퇴 확정 시 사용자의 쿠폰을 최종 삭제합니다.")
                                .pathParameters(parameterWithName("userId").description("사용자 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(couponCleanupUseCase).finalizeByUserId(1L);
    }

    @Test
    @DisplayName("포인트 최종 삭제(finalize) - 성공")
    void finalizePoints_success() throws Exception {
        willDoNothing().given(pointCleanupUseCase).finalizeByUserId(1L);

        mockMvc.perform(post("/internal/withdraw/points/{userId}/finalize", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("internal-commerce-point-finalize",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("내부 커머스 API").summary("포인트 최종 삭제")
                                .description("탈퇴 확정 시 사용자의 포인트를 최종 삭제합니다.")
                                .pathParameters(parameterWithName("userId").description("사용자 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(pointCleanupUseCase).finalizeByUserId(1L);
    }
}
