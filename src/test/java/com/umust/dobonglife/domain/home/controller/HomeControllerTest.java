package com.umust.dobonglife.domain.home.controller;

import com.umust.dobonglife.domain.banners.service.dto.BannerSummaryResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionSummaryItem;
import com.umust.dobonglife.domain.home.controller.dto.response.HomeSummaryResponse;
import com.umust.dobonglife.domain.home.service.HomeService;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.external.firebase.FirebaseConfig;
import com.umust.dobonglife.global.support.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class HomeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    HomeService homeService;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Nested
    @DisplayName("GET /api/home")
    class ViewHome {

        @Test
        @DisplayName("홈 조회 성공")
        @WithMockCustomUser
        void 홈_조회_성공() throws Exception {
            BannerSummaryResponse banner = new BannerSummaryResponse(
                    1L, "배너 제목", "배너 설명", "https://example.com/banner"
            );
            CursorResponse<BannerSummaryResponse> banners =
                    new CursorResponse<>(List.of(banner), false);

            PromotionSummaryItem promotion = new PromotionSummaryItem(
                    1L, "음식", "프로모션 제목", List.of("img1.jpg", "img2.jpg")
            );
            CursorResponse<PromotionSummaryItem> promotions =
                    new CursorResponse<>(List.of(promotion), false);

            HomeSummaryResponse response = new HomeSummaryResponse(banners, promotions);

            when(homeService.getHomeSummary(isNull(), eq(3)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/home"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.banners.content[0].title").value("배너 제목"))
                    .andExpect(jsonPath("$.data.banners.content[0].description").value("배너 설명"))
                    .andExpect(jsonPath("$.data.banners.content[0].link").value("https://example.com/banner"))
                    .andExpect(jsonPath("$.data.promotions.content[0].promotionId").value(1))
                    .andExpect(jsonPath("$.data.promotions.content[0].category").value("음식"))
                    .andExpect(jsonPath("$.data.promotions.content[0].title").value("프로모션 제목"))
                    .andExpect(jsonPath("$.data.promotions.content[0].imgUrls[0]").value("img1.jpg"))
                    .andDo(print())
                    .andDo(document("home-main",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(
                                    parameterWithName("lastId").optional()
                                            .description("커서 - 마지막 프로모션 ID (첫 요청 시 생략)"),
                                    parameterWithName("size").optional()
                                            .description("조회 개수 (기본값: 3)")
                            ),
                            responseFields(
                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),

                                    fieldWithPath("data.banners.content[]").type(JsonFieldType.ARRAY).description("배너 목록"),
                                    fieldWithPath("data.banners.content[].title").type(JsonFieldType.STRING).description("배너 제목"),
                                    fieldWithPath("data.banners.content[].description").type(JsonFieldType.STRING).description("배너 설명"),
                                    fieldWithPath("data.banners.content[].link").type(JsonFieldType.STRING).description("배너 링크"),
                                    fieldWithPath("data.banners.lastId").type(JsonFieldType.NUMBER).description("마지막 배너 ID"),
                                    fieldWithPath("data.banners.hasNext").type(JsonFieldType.BOOLEAN).description("배너 다음 페이지 여부"),

                                    fieldWithPath("data.promotions.content[]").type(JsonFieldType.ARRAY).description("프로모션 목록"),
                                    fieldWithPath("data.promotions.content[].promotionId").type(JsonFieldType.NUMBER).description("프로모션 ID"),
                                    fieldWithPath("data.promotions.content[].category").type(JsonFieldType.STRING).description("카테고리"),
                                    fieldWithPath("data.promotions.content[].title").type(JsonFieldType.STRING).description("프로모션 제목"),
                                    fieldWithPath("data.promotions.content[].imgUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                    fieldWithPath("data.promotions.lastId").type(JsonFieldType.NUMBER).description("마지막 프로모션 ID"),
                                    fieldWithPath("data.promotions.hasNext").type(JsonFieldType.BOOLEAN).description("프로모션 다음 페이지 여부")
                            )
                    ));
        }

        @Test
        @DisplayName("lastId 파라미터 전달 성공")
        @WithMockCustomUser
        void lastId_파라미터_전달_성공() throws Exception {
            CursorResponse<BannerSummaryResponse> banners =
                    new CursorResponse<>(List.of(), false);
            CursorResponse<PromotionSummaryItem> promotions =
                    new CursorResponse<>(List.of(), false);
            HomeSummaryResponse response = new HomeSummaryResponse(banners, promotions);

            when(homeService.getHomeSummary(eq(5L), eq(3)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/home").param("lastId", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("빈 홈 데이터 조회 성공")
        @WithMockCustomUser
        void 빈_홈_데이터_조회_성공() throws Exception {
            CursorResponse<BannerSummaryResponse> banners =
                    new CursorResponse<>(List.of(), false);
            CursorResponse<PromotionSummaryItem> promotions =
                    new CursorResponse<>(List.of(), false);
            HomeSummaryResponse response = new HomeSummaryResponse(banners, promotions);

            when(homeService.getHomeSummary(isNull(), eq(3)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/home"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.banners.content").isEmpty())
                    .andExpect(jsonPath("$.data.promotions.content").isEmpty());
        }
    }
}
