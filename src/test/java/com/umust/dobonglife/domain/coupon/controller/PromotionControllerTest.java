package com.umust.dobonglife.domain.coupon.controller;

import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionGetResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionItem;
import com.umust.dobonglife.domain.coupon.controller.dto.response.UsedCouponResponse;
import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.service.PreSetService;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class PromotionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PromotionService promotionService;

    @MockitoBean
    PreSetService preSetService;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Nested
    @DisplayName("GET /api/promotion")
    class GetPromotion {

        @Test
        @DisplayName("프로모션 조회 성공")
        @WithMockCustomUser
        void 프로모션_조회_성공() throws Exception {
            PromotionItem item = new PromotionItem(
                    1L, 10L, "도봉 맛집", "09:00~22:00",
                    "음식", "할인 이벤트", "10% 할인 이벤트입니다",
                    List.of("img1.jpg", "img2.jpg"),
                    DiscountType.PERCENT, BigDecimal.valueOf(10),
                    100L, 10000L, 50000L,
                    LocalDate.of(2026, 12, 31)
            );
            CursorResponse<PromotionItem> promotions =
                    new CursorResponse<>(List.of(item), false);

            PromotionGetResponse response = new PromotionGetResponse(false, promotions);

            when(promotionService.getPromotionWithBlocked(eq(1L), isNull(), eq(2)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/promotion"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.isBlockedUser").value(false))
                    .andExpect(jsonPath("$.data.promotions.content[0].promotionId").value(1))
                    .andExpect(jsonPath("$.data.promotions.content[0].placeName").value("도봉 맛집"))
                    .andExpect(jsonPath("$.data.promotions.content[0].title").value("할인 이벤트"))
                    .andExpect(jsonPath("$.data.promotions.content[0].discountType").value("PERCENT"))
                    .andExpect(jsonPath("$.data.promotions.content[0].point").value(100))
                    .andDo(print())
                    .andDo(document("promotion-list",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("프로모션 API")
                                    .summary("프로모션 목록 조회")
                                    .description("프로모션 목록을 조회합니다.")
                                    .queryParameters(
                                            parameterWithName("lastId").optional()
                                                    .description("커서 - 마지막 프로모션 ID (첫 요청 시 생략)"),
                                            parameterWithName("size").optional()
                                                    .description("조회 개수 (기본값: 2)")
                                    )
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),

                                            fieldWithPath("data.isBlockedUser").type(JsonFieldType.BOOLEAN).description("차단 사용자 여부"),
                                            fieldWithPath("data.promotions.content[]").type(JsonFieldType.ARRAY).description("프로모션 목록"),
                                            fieldWithPath("data.promotions.content[].promotionId").type(JsonFieldType.NUMBER).description("프로모션 ID"),
                                            fieldWithPath("data.promotions.content[].placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                            fieldWithPath("data.promotions.content[].placeName").type(JsonFieldType.STRING).description("장소 이름"),
                                            fieldWithPath("data.promotions.content[].operatingHour").type(JsonFieldType.STRING).description("운영 시간"),
                                            fieldWithPath("data.promotions.content[].category").type(JsonFieldType.STRING).description("카테고리"),
                                            fieldWithPath("data.promotions.content[].title").type(JsonFieldType.STRING).description("프로모션 제목"),
                                            fieldWithPath("data.promotions.content[].description").type(JsonFieldType.STRING).description("프로모션 설명"),
                                            fieldWithPath("data.promotions.content[].imgUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                            fieldWithPath("data.promotions.content[].discountType").type(JsonFieldType.STRING).description("할인 유형 (PERCENT/AMOUNT)"),
                                            fieldWithPath("data.promotions.content[].discountValue").type(JsonFieldType.NUMBER).description("할인 값"),
                                            fieldWithPath("data.promotions.content[].point").type(JsonFieldType.NUMBER).description("필요 포인트"),
                                            fieldWithPath("data.promotions.content[].minPrice").type(JsonFieldType.NUMBER).description("최소 금액"),
                                            fieldWithPath("data.promotions.content[].maxPrice").type(JsonFieldType.NUMBER).description("최대 금액"),
                                            fieldWithPath("data.promotions.content[].endDate").type(JsonFieldType.STRING).description("종료일"),
                                            fieldWithPath("data.promotions.lastId").type(JsonFieldType.NUMBER).description("마지막 프로모션 ID"),
                                            fieldWithPath("data.promotions.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                                    )
                                    .build()
                            )
                    ));
        }

        @Test
        @DisplayName("빈 프로모션 목록 조회 성공")
        @WithMockCustomUser
        void 빈_프로모션_목록_조회_성공() throws Exception {
            CursorResponse<PromotionItem> promotions =
                    new CursorResponse<>(List.of(), false);
            PromotionGetResponse response = new PromotionGetResponse(false, promotions);

            when(promotionService.getPromotionWithBlocked(eq(1L), isNull(), eq(2)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/promotion"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.promotions.content").isEmpty());
        }

        @Test
        @DisplayName("차단된 사용자 프로모션 조회 성공")
        @WithMockCustomUser
        void 차단된_사용자_프로모션_조회_성공() throws Exception {
            CursorResponse<PromotionItem> promotions =
                    new CursorResponse<>(List.of(), false);
            PromotionGetResponse response = new PromotionGetResponse(true, promotions);

            when(promotionService.getPromotionWithBlocked(eq(1L), isNull(), eq(2)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/promotion"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.isBlockedUser").value(true));
        }
    }

    @Nested
    @DisplayName("POST /api/promotion/{promotionId}")
    class ChangePointToCoupon {

        @Test
        @DisplayName("포인트로 쿠폰 발급 성공")
        @WithMockCustomUser
        void 포인트로_쿠폰_발급_성공() throws Exception {
            UsedCouponResponse response = new UsedCouponResponse(1L, CouponStatus.AVAILABLE);

            when(promotionService.changePointToCoupon(eq(1L), eq(10L)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/promotion/{promotionId}", 10L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.couponId").value(1))
                    .andExpect(jsonPath("$.data.couponStatus").value("AVAILABLE"))
                    .andDo(print())
                    .andDo(document("promotion-exchange-coupon",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("프로모션 API")
                                    .summary("포인트로 쿠폰 발급")
                                    .description("포인트를 사용하여 쿠폰을 발급합니다.")
                                    .pathParameters(
                                            parameterWithName("promotionId").description("프로모션 ID")
                                    )
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data.couponId").type(JsonFieldType.NUMBER).description("발급된 쿠폰 ID"),
                                            fieldWithPath("data.couponStatus").type(JsonFieldType.STRING).description("쿠폰 상태 (AVAILABLE)")
                                    )
                                    .build()
                            )
                    ));
        }
    }
}
