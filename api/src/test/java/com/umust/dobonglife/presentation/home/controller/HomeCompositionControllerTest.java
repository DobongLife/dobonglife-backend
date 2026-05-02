package com.umust.dobonglife.presentation.home.controller;

import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.composition.HomeFacade;
import com.umust.dobonglife.global.composition.dto.response.HomeResponse;
import com.umust.dobonglife.global.port.dto.content.BannerInfo;
import com.umust.dobonglife.global.port.dto.commerce.PromotionSummaryInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.SimpleType.INTEGER;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeCompositionController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class HomeCompositionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    HomeFacade homeFacade;

    @Nested
    @DisplayName("GET /api/home — 병렬 조회")
    class ParallelFetchTest {

        @Test
        @DisplayName("배너 + 프로모션 병렬 조회 성공")
        void 홈_병렬_조회_성공() throws Exception {
            BannerInfo banner = new BannerInfo(1L, "도봉 축제", "봄맞이 축제", "https://img.com/1.jpg", "https://link.com");
            PromotionSummaryInfo promotion = new PromotionSummaryInfo(
                    1L, "음식", "떡볶이 할인", "맛있는 떡볶이", "https://img.com/t.jpg",
                    List.of("https://img.com/1.jpg"), "PERCENT", 10L, 100L, 5000L, 10000L,
                    LocalDate.of(2026, 5, 31)
            );

            HomeResponse response = new HomeResponse(
                    List.of(banner),
                    new CursorResponse<>(List.of(promotion), false)
            );

            when(homeFacade.getHome(isNull(), eq(10))).thenReturn(response);

            mockMvc.perform(get("/api/home"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.banners[0].title").value("도봉 축제"))
                    .andExpect(jsonPath("$.data.promotions.content[0].title").value("떡볶이 할인"))
                    .andDo(document("home-parallel-fetch",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("홈 API")
                                    .summary("홈 화면 병렬 조회")
                                    .description("배너(Content)와 프로모션(Commerce)을 CompletableFuture로 병렬 조회합니다.")
                                    .queryParameters(
                                            parameterWithName("lastId").optional().description("커서 ID").type(INTEGER),
                                            parameterWithName("size").optional().description("조회 개수 (기본값: 10)").type(INTEGER)
                                    )
                                    .responseFields(
                                            fieldWithPath("success").type(BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("message").type(STRING).description("응답 메시지"),
                                            fieldWithPath("data.banners[]").type(ARRAY).description("배너 목록 (Content 서비스)"),
                                            fieldWithPath("data.banners[].bannerId").type(NUMBER).description("배너 ID"),
                                            fieldWithPath("data.banners[].title").type(STRING).description("배너 제목"),
                                            fieldWithPath("data.banners[].description").type(STRING).description("배너 설명"),
                                            fieldWithPath("data.banners[].imageUrl").type(STRING).description("배너 이미지"),
                                            fieldWithPath("data.banners[].link").type(STRING).description("배너 링크"),
                                            fieldWithPath("data.promotions.content[]").type(ARRAY).description("프로모션 목록 (Commerce 서비스)"),
                                            fieldWithPath("data.promotions.content[].promotionId").type(NUMBER).description("프로모션 ID"),
                                            fieldWithPath("data.promotions.content[].category").type(STRING).description("카테고리"),
                                            fieldWithPath("data.promotions.content[].title").type(STRING).description("제목"),
                                            fieldWithPath("data.promotions.content[].description").type(STRING).description("설명"),
                                            fieldWithPath("data.promotions.content[].thumbnailUrl").type(STRING).description("썸네일"),
                                            fieldWithPath("data.promotions.content[].imageUrls").type(ARRAY).description("이미지 URL"),
                                            fieldWithPath("data.promotions.content[].discountType").type(STRING).description("할인 타입"),
                                            fieldWithPath("data.promotions.content[].discountValue").type(NUMBER).description("할인 값"),
                                            fieldWithPath("data.promotions.content[].point").type(NUMBER).description("필요 포인트"),
                                            fieldWithPath("data.promotions.content[].minPrice").type(NUMBER).description("최소 금액"),
                                            fieldWithPath("data.promotions.content[].maxPrice").type(NUMBER).description("최대 금액"),
                                            fieldWithPath("data.promotions.content[].endDate").type(STRING).description("종료일"),
                                            fieldWithPath("data.promotions.lastId").type(NUMBER).description("마지막 ID"),
                                            fieldWithPath("data.promotions.hasNext").type(BOOLEAN).description("다음 페이지 여부")
                                    )
                                    .build()
                            )
                    ));
        }

        @Test
        @DisplayName("서킷 브레이커 Fallback — 빈 응답 반환")
        void 서킷_오픈_시_빈_응답_반환() throws Exception {
            HomeResponse fallbackResponse = new HomeResponse(
                    Collections.emptyList(),
                    CursorResponse.empty()
            );

            when(homeFacade.getHome(isNull(), eq(10))).thenReturn(fallbackResponse);

            mockMvc.perform(get("/api/home"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.banners").isEmpty())
                    .andExpect(jsonPath("$.data.promotions.content").isEmpty())
                    .andExpect(jsonPath("$.data.promotions.hasNext").value(false))
                    .andDo(document("home-circuit-breaker-fallback",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("홈 API")
                                    .summary("홈 조회 — 서킷 브레이커 Fallback")
                                    .description("Content/Commerce 서비스 장애 시 빈 응답을 반환합니다. 서킷이 OPEN 상태일 때 동작합니다.")
                                    .build()
                            )
                    ));
        }
    }
}
