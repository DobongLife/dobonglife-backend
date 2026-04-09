package com.umust.dobonglife.commerce.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.commerce.facade.PromotionFacade;
import com.umust.dobonglife.commerce.facade.PromotionFacade.PromotionWithBlockedResponse;
import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.coupon.application.ExchangeOrchestrator;
import com.umust.dobonglife.domain.coupon.application.dto.ExchangeRequest;
import com.umust.dobonglife.domain.coupon.application.dto.ExchangeResponse;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionSummary;
import com.umust.dobonglife.domain.promotion.presentation.dto.request.PromotionUpdateRequest;
import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionPresetResponse;
import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionRegisterResponse;
import com.umust.dobonglife.domain.promotion.presentation.dto.response.PromotionUpdateResponse;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PromotionCompositionController.class, excludeAutoConfiguration = {OAuth2ClientAutoConfiguration.class, OAuth2ClientWebSecurityAutoConfiguration.class, SecurityAutoConfiguration.class})
@Import(TestWebMvcConfig.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class PromotionCompositionControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean PromotionFacade promotionFacade;
    @MockitoBean ExchangeOrchestrator exchangeOrchestrator;
    @MockitoBean AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;
    @MockitoBean TokenExtractor tokenExtractor;

    @Test
    @DisplayName("차단 포함 프로모션 목록 조회 - 성공")
    @WithMockCustomUser
    void getPromotionsWithBlocked_success() throws Exception {
        CursorResponse<PromotionSummary> promotions = new CursorResponse<>(
                List.of(new PromotionSummary(1L, "CAFE", "봄맞이 할인", "봄 시즌 특별 할인",
                        "https://img.test/promo.jpg", List.of(), "PERCENT", 10L, 1000L, 0L, 5000L, LocalDate.now().plusDays(30))),
                false);
        PromotionWithBlockedResponse response = new PromotionWithBlockedResponse(false, promotions);

        given(promotionFacade.getPromotionsWithBlocked(eq(1L), any(), anyInt())).willReturn(response);

        mockMvc.perform(get("/api/composition/promotion").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.blocked").value(false))
                .andExpect(jsonPath("$.data.promotions.content[0].title").value("봄맞이 할인"))
                .andDo(print())
                .andDo(document("promotion-composition-get-with-blocked",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("프로모션 조합 API").summary("차단 포함 프로모션 목록 조회")
                                .description("사용자 차단 여부와 함께 프로모션 목록을 조회합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.blocked").type(JsonFieldType.BOOLEAN).description("사용자 차단 여부"),
                                        fieldWithPath("data.promotions.content[].promotionId").type(JsonFieldType.NUMBER).description("프로모션 ID"),
                                        fieldWithPath("data.promotions.content[].category").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("data.promotions.content[].title").type(JsonFieldType.STRING).description("프로모션 제목"),
                                        fieldWithPath("data.promotions.content[].description").type(JsonFieldType.STRING).description("설명"),
                                        fieldWithPath("data.promotions.content[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                                        fieldWithPath("data.promotions.content[].imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                        fieldWithPath("data.promotions.content[].discountType").type(JsonFieldType.STRING).description("할인 타입"),
                                        fieldWithPath("data.promotions.content[].discountValue").type(JsonFieldType.NUMBER).description("할인값"),
                                        fieldWithPath("data.promotions.content[].point").type(JsonFieldType.NUMBER).description("필요 포인트"),
                                        fieldWithPath("data.promotions.content[].minPrice").type(JsonFieldType.NUMBER).description("최소 가격"),
                                        fieldWithPath("data.promotions.content[].maxPrice").type(JsonFieldType.NUMBER).description("최대 가격"),
                                        fieldWithPath("data.promotions.content[].endDate").type(JsonFieldType.STRING).description("종료일"),
                                        fieldWithPath("data.promotions.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                                        fieldWithPath("data.promotions.lastId").type(JsonFieldType.NUMBER).description("마지막 ID"))
                                .build())
                ));

        verify(promotionFacade).getPromotionsWithBlocked(eq(1L), any(), anyInt());
    }

    // NOTE: 멀티파트 업로드 테스트는 @RequestPart validation 이슈로 통합 테스트에서 검증

    @Test
    @DisplayName("프로모션 수정 - 성공")
    @WithMockCustomUser
    void updatePromotion_success() throws Exception {
        PromotionUpdateResponse response = new PromotionUpdateResponse("수정된 제목", "수정된 설명", 200L);

        given(promotionFacade.modifyPromotion(any(), eq(1L), eq(1L))).willReturn(response);

        PromotionUpdateRequest request = new PromotionUpdateRequest("수정된 제목", "수정된 설명", 200L);

        mockMvc.perform(patch("/api/composition/promotion/update/{promotionId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("수정된 제목"))
                .andExpect(jsonPath("$.data.totalQuantity").value(200))
                .andDo(print())
                .andDo(document("promotion-composition-update",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("프로모션 조합 API").summary("프로모션 수정")
                                .description("프로모션 정보를 수정합니다.")
                                .pathParameters(parameterWithName("promotionId").description("프로모션 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("프로모션 제목"),
                                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("프로모션 설명"),
                                        fieldWithPath("data.totalQuantity").type(JsonFieldType.NUMBER).description("총 수량"))
                                .build())
                ));

        verify(promotionFacade).modifyPromotion(any(), eq(1L), eq(1L));
    }

    @Test
    @DisplayName("프로모션 프리셋 조회 - 성공")
    @WithMockCustomUser
    void getPreset_success() throws Exception {
        LocalDate now = LocalDate.now();
        PromotionPresetResponse response = new PromotionPresetResponse(
                1L, "CAFE", "카페 프리셋 설명", 1000L, "https://img.test/preset.jpg",
                "PERCENT", 10L, 0L, 5000L, 30L, now, now.plusDays(30));

        given(promotionFacade.getPreset(eq(1L))).willReturn(response);

        mockMvc.perform(get("/api/composition/promotion/preset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.category").value("CAFE"))
                .andExpect(jsonPath("$.data.discountType").value("PERCENT"))
                .andDo(print())
                .andDo(document("promotion-composition-get-preset",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("프로모션 조합 API").summary("프로모션 프리셋 조회")
                                .description("사업자 카테고리에 맞는 프로모션 프리셋을 조회합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("프리셋 ID"),
                                        fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("설명"),
                                        fieldWithPath("data.point").type(JsonFieldType.NUMBER).description("포인트"),
                                        fieldWithPath("data.imageUrl").type(JsonFieldType.STRING).description("이미지 URL"),
                                        fieldWithPath("data.discountType").type(JsonFieldType.STRING).description("할인 타입"),
                                        fieldWithPath("data.discountValue").type(JsonFieldType.NUMBER).description("할인값"),
                                        fieldWithPath("data.minPrice").type(JsonFieldType.NUMBER).description("최소 가격"),
                                        fieldWithPath("data.maxPrice").type(JsonFieldType.NUMBER).description("최대 가격"),
                                        fieldWithPath("data.validPeriod").type(JsonFieldType.NUMBER).description("유효 기간(일)"),
                                        fieldWithPath("data.issueStartDate").type(JsonFieldType.STRING).description("발급 시작일"),
                                        fieldWithPath("data.issueEndDate").type(JsonFieldType.STRING).description("발급 종료일"))
                                .build())
                ));

        verify(promotionFacade).getPreset(eq(1L));
    }

    @Test
    @DisplayName("쿠폰 교환 - 성공")
    @WithMockCustomUser
    void exchangeCoupon_success() throws Exception {
        ExchangeResponse response = new ExchangeResponse(100L, 50L, "COMPLETED");

        given(exchangeOrchestrator.execute(any(ExchangeRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/composition/promotion/{promotionId}/exchange", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sagaId").value(100))
                .andExpect(jsonPath("$.data.couponId").value(50))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andDo(print())
                .andDo(document("promotion-composition-exchange",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("프로모션 조합 API").summary("쿠폰 교환")
                                .description("포인트를 사용하여 프로모션 쿠폰을 교환합니다.")
                                .pathParameters(parameterWithName("promotionId").description("프로모션 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.sagaId").type(JsonFieldType.NUMBER).description("교환 사가 ID"),
                                        fieldWithPath("data.couponId").type(JsonFieldType.NUMBER).description("발급된 쿠폰 ID"),
                                        fieldWithPath("data.status").type(JsonFieldType.STRING).description("교환 상태"))
                                .build())
                ));

        verify(exchangeOrchestrator).execute(any(ExchangeRequest.class));
    }
}
