package com.umust.dobonglife.commerce.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.commerce.controller.CouponCompositionController.CouponUseRequest;
import com.umust.dobonglife.commerce.facade.CouponFacade;
import com.umust.dobonglife.commerce.facade.CouponFacade.CouponSummary;
import com.umust.dobonglife.commerce.facade.CouponFacade.CouponUsedResponse;
import com.umust.dobonglife.commerce.facade.CouponFacade.MyCouponGetResponse;
import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.coupon.application.dto.MyCouponStatus;
import com.umust.dobonglife.global.common.response.CursorResponse;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CouponCompositionController.class, excludeAutoConfiguration = {OAuth2ClientAutoConfiguration.class, OAuth2ClientWebSecurityAutoConfiguration.class, SecurityAutoConfiguration.class})
@Import(TestWebMvcConfig.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class CouponCompositionControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean CouponFacade couponFacade;
    @MockitoBean AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;
    @MockitoBean TokenExtractor tokenExtractor;

    @Test
    @DisplayName("내 쿠폰 목록 조회 - 성공")
    @WithMockCustomUser
    void getMyCoupon_success() throws Exception {
        CursorResponse<CouponSummary> couponPage = new CursorResponse<>(
                List.of(new CouponSummary(10L, 1L, "봄맞이 할인 쿠폰", "AVAILABLE")),
                false);
        MyCouponStatus status = MyCouponStatus.of(3L, 1L, 0L);
        MyCouponGetResponse response = new MyCouponGetResponse(status, couponPage);

        given(couponFacade.getMyCoupon(eq(1L), any(), anyInt())).willReturn(response);

        mockMvc.perform(get("/api/composition/coupon/my").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status.available").value(3))
                .andExpect(jsonPath("$.data.coupons.content[0].promotionTitle").value("봄맞이 할인 쿠폰"))
                .andDo(print())
                .andDo(document("coupon-composition-get-my",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("쿠폰 조합 API").summary("내 쿠폰 목록 조회")
                                .description("로그인 사용자의 쿠폰 목록과 상태를 조회합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.status.available").type(JsonFieldType.NUMBER).description("사용 가능 쿠폰 수"),
                                        fieldWithPath("data.status.used").type(JsonFieldType.NUMBER).description("사용 완료 쿠폰 수"),
                                        fieldWithPath("data.status.expired").type(JsonFieldType.NUMBER).description("만료 쿠폰 수"),
                                        fieldWithPath("data.coupons.content[].couponId").type(JsonFieldType.NUMBER).description("쿠폰 ID"),
                                        fieldWithPath("data.coupons.content[].promotionId").type(JsonFieldType.NUMBER).description("프로모션 ID"),
                                        fieldWithPath("data.coupons.content[].promotionTitle").type(JsonFieldType.STRING).description("프로모션 제목"),
                                        fieldWithPath("data.coupons.content[].status").type(JsonFieldType.STRING).description("쿠폰 상태"),
                                        fieldWithPath("data.coupons.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                                        fieldWithPath("data.coupons.lastId").type(JsonFieldType.NUMBER).description("마지막 쿠폰 ID"))
                                .build())
                ));

        verify(couponFacade).getMyCoupon(eq(1L), any(), anyInt());
    }

    @Test
    @DisplayName("쿠폰 사용 - 성공")
    @WithMockCustomUser
    void useCoupon_success() throws Exception {
        CouponUsedResponse response = new CouponUsedResponse(10L);
        given(couponFacade.useCoupon(eq(1L), eq("SPRING2026"), eq(10L), eq(1L))).willReturn(response);

        CouponUseRequest request = new CouponUseRequest(1L, "SPRING2026");

        mockMvc.perform(post("/api/composition/coupon/use/{couponId}", 10L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.couponId").value(10))
                .andDo(print())
                .andDo(document("coupon-composition-use",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("쿠폰 조합 API").summary("쿠폰 사용")
                                .description("쿠폰을 사용합니다.")
                                .pathParameters(parameterWithName("couponId").description("쿠폰 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.couponId").type(JsonFieldType.NUMBER).description("사용된 쿠폰 ID"))
                                .build())
                ));

        verify(couponFacade).useCoupon(eq(1L), eq("SPRING2026"), eq(10L), eq(1L));
    }
}
