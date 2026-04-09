package com.umust.dobonglife.commerce.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.promotion.application.PromotionService;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionAdSummary;
import com.umust.dobonglife.domain.promotion.application.dto.PromotionSummary;
import com.umust.dobonglife.global.common.response.CursorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PromotionController.class, excludeAutoConfiguration = {OAuth2ClientAutoConfiguration.class, OAuth2ClientWebSecurityAutoConfiguration.class, SecurityAutoConfiguration.class})
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class PromotionControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean PromotionService promotionService;
    @MockitoBean AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;
    @MockitoBean TokenExtractor tokenExtractor;

    @Test
    @DisplayName("프로모션 목록 조회 - 성공")
    void getPromotions_success() throws Exception {
        CursorResponse<PromotionSummary> response = new CursorResponse<>(
                List.of(new PromotionSummary(1L, "CAFE", "봄맞이 할인", "봄 시즌 특별 할인",
                        "https://img.test/promo.jpg", List.of(), "PERCENT", 10L, 1000L, 0L, 5000L, LocalDate.now().plusDays(30))),
                false);

        given(promotionService.getPromotions(any(), anyInt())).willReturn(response);

        mockMvc.perform(get("/api/promotion").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("봄맞이 할인"))
                .andDo(print())
                .andDo(document("promotion-get-list",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("프로모션 API").summary("프로모션 목록 조회")
                                .description("프로모션 목록을 커서 기반으로 조회합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.content[].promotionId").type(JsonFieldType.NUMBER).description("프로모션 ID"),
                                        fieldWithPath("data.content[].category").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("프로모션 제목"),
                                        fieldWithPath("data.content[].description").type(JsonFieldType.STRING).description("설명"),
                                        fieldWithPath("data.content[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                                        fieldWithPath("data.content[].imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                        fieldWithPath("data.content[].discountType").type(JsonFieldType.STRING).description("할인 타입"),
                                        fieldWithPath("data.content[].discountValue").type(JsonFieldType.NUMBER).description("할인값"),
                                        fieldWithPath("data.content[].point").type(JsonFieldType.NUMBER).description("필요 포인트"),
                                        fieldWithPath("data.content[].minPrice").type(JsonFieldType.NUMBER).description("최소 가격"),
                                        fieldWithPath("data.content[].maxPrice").type(JsonFieldType.NUMBER).description("최대 가격"),
                                        fieldWithPath("data.content[].endDate").type(JsonFieldType.STRING).description("종료일"),
                                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                                        fieldWithPath("data.lastId").type(JsonFieldType.NUMBER).description("마지막 ID"))
                                .build())
                ));
    }

    @Test
    @DisplayName("광고 프로모션 목록 조회 - 성공")
    void getAdPromotions_success() throws Exception {
        CursorResponse<PromotionAdSummary> response = new CursorResponse<>(
                List.of(new PromotionAdSummary(1L, "CAFE", "도봉 카페 광고", "카페 광고 설명", "https://img.test/ad.jpg")),
                false);

        given(promotionService.getAdPromotions(any(), anyInt())).willReturn(response);

        mockMvc.perform(get("/api/promotion/ad").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("도봉 카페 광고"))
                .andDo(print())
                .andDo(document("promotion-get-ad-list",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("프로모션 API").summary("광고 프로모션 목록 조회")
                                .description("광고 프로모션 목록을 커서 기반으로 조회합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.content[].promotionId").type(JsonFieldType.NUMBER).description("프로모션 ID"),
                                        fieldWithPath("data.content[].category").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("프로모션 제목"),
                                        fieldWithPath("data.content[].description").type(JsonFieldType.STRING).description("설명"),
                                        fieldWithPath("data.content[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                                        fieldWithPath("data.lastId").type(JsonFieldType.NUMBER).description("마지막 ID"))
                                .build())
                ));
    }
}
