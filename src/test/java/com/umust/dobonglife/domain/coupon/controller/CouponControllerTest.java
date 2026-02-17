package com.umust.dobonglife.domain.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.domain.coupon.controller.dto.request.CouponCodeRequest;
import com.umust.dobonglife.domain.coupon.controller.dto.response.CouponItem;
import com.umust.dobonglife.domain.coupon.controller.dto.response.MyCouponResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.MyCouponStatus;
import com.umust.dobonglife.domain.coupon.controller.dto.response.UsedCouponResponse;
import com.umust.dobonglife.domain.coupon.domain.constant.CouponStatus;
import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.service.CouponService;
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
import org.springframework.http.MediaType;
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

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class CouponControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CouponService couponService;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Nested
    @DisplayName("GET /api/coupon/my")
    class GetMyCoupon {

        @Test
        @DisplayName("내 쿠폰 조회 성공")
        @WithMockCustomUser
        void 내_쿠폰_조회_성공() throws Exception {
            MyCouponStatus status = new MyCouponStatus(3, 1, 0);

            CouponItem couponItem = new CouponItem(
                    1L, 10L, "도봉 맛집", "09:00~22:00",
                    100L, "음식", "할인 쿠폰", "10% 할인 쿠폰입니다",
                    List.of("img1.jpg", "img2.jpg"),
                    DiscountType.PERCENT, BigDecimal.valueOf(10),
                    10000L, 50000L,
                    LocalDate.of(2026, 12, 31),
                    CouponStatus.AVAILABLE
            );
            CursorResponse<CouponItem> couponList =
                    new CursorResponse<>(List.of(couponItem), false);

            MyCouponResponse response = new MyCouponResponse(status, couponList);

            when(couponService.getMyCoupon(eq(1L), isNull(), eq(2)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/coupon/my"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.myCouponStatus.available").value(3))
                    .andExpect(jsonPath("$.data.myCouponStatus.used").value(1))
                    .andExpect(jsonPath("$.data.myCouponStatus.expired").value(0))
                    .andExpect(jsonPath("$.data.myCouponList.content[0].couponId").value(1))
                    .andExpect(jsonPath("$.data.myCouponList.content[0].placeName").value("도봉 맛집"))
                    .andExpect(jsonPath("$.data.myCouponList.content[0].title").value("할인 쿠폰"))
                    .andExpect(jsonPath("$.data.myCouponList.content[0].discountType").value("PERCENT"))
                    .andExpect(jsonPath("$.data.myCouponList.content[0].couponStatus").value("AVAILABLE"))
                    .andDo(print())
                    .andDo(document("coupon-my-list",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(
                                    parameterWithName("lastId").optional()
                                            .description("커서 - 마지막 쿠폰 ID (첫 요청 시 생략)"),
                                    parameterWithName("size").optional()
                                            .description("조회 개수 (기본값: 2)")
                            ),
                            responseFields(
                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),

                                    fieldWithPath("data.myCouponStatus.available").type(JsonFieldType.NUMBER).description("사용 가능 쿠폰 수"),
                                    fieldWithPath("data.myCouponStatus.used").type(JsonFieldType.NUMBER).description("사용 완료 쿠폰 수"),
                                    fieldWithPath("data.myCouponStatus.expired").type(JsonFieldType.NUMBER).description("만료 쿠폰 수"),

                                    fieldWithPath("data.myCouponList.content[]").type(JsonFieldType.ARRAY).description("쿠폰 목록"),
                                    fieldWithPath("data.myCouponList.content[].couponId").type(JsonFieldType.NUMBER).description("쿠폰 ID"),
                                    fieldWithPath("data.myCouponList.content[].placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                    fieldWithPath("data.myCouponList.content[].placeName").type(JsonFieldType.STRING).description("장소 이름"),
                                    fieldWithPath("data.myCouponList.content[].operatingHour").type(JsonFieldType.STRING).description("운영 시간"),
                                    fieldWithPath("data.myCouponList.content[].promotionId").type(JsonFieldType.NUMBER).description("프로모션 ID"),
                                    fieldWithPath("data.myCouponList.content[].category").type(JsonFieldType.STRING).description("카테고리"),
                                    fieldWithPath("data.myCouponList.content[].title").type(JsonFieldType.STRING).description("쿠폰 제목"),
                                    fieldWithPath("data.myCouponList.content[].description").type(JsonFieldType.STRING).description("쿠폰 설명"),
                                    fieldWithPath("data.myCouponList.content[].imgUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                    fieldWithPath("data.myCouponList.content[].discountType").type(JsonFieldType.STRING).description("할인 유형 (PERCENT/AMOUNT)"),
                                    fieldWithPath("data.myCouponList.content[].discountValue").type(JsonFieldType.NUMBER).description("할인 값"),
                                    fieldWithPath("data.myCouponList.content[].minPrice").type(JsonFieldType.NUMBER).description("최소 금액"),
                                    fieldWithPath("data.myCouponList.content[].maxPrice").type(JsonFieldType.NUMBER).description("최대 금액"),
                                    fieldWithPath("data.myCouponList.content[].endDate").type(JsonFieldType.STRING).description("만료일"),
                                    fieldWithPath("data.myCouponList.content[].couponStatus").type(JsonFieldType.STRING).description("쿠폰 상태 (AVAILABLE/USED/EXPIRED/DISABLED)"),
                                    fieldWithPath("data.myCouponList.lastId").type(JsonFieldType.NUMBER).description("마지막 쿠폰 ID"),
                                    fieldWithPath("data.myCouponList.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                            )
                    ));
        }

        @Test
        @DisplayName("빈 쿠폰 목록 조회 성공")
        @WithMockCustomUser
        void 빈_쿠폰_목록_조회_성공() throws Exception {
            MyCouponStatus status = new MyCouponStatus(0, 0, 0);
            CursorResponse<CouponItem> couponList =
                    new CursorResponse<>(List.of(), false);
            MyCouponResponse response = new MyCouponResponse(status, couponList);

            when(couponService.getMyCoupon(eq(1L), isNull(), eq(2)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/coupon/my"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.myCouponStatus.available").value(0))
                    .andExpect(jsonPath("$.data.myCouponList.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("POST /api/coupon/my")
    class UseMyCoupon {

        @Test
        @DisplayName("쿠폰 사용 성공")
        @WithMockCustomUser
        void 쿠폰_사용_성공() throws Exception {
            CouponCodeRequest request = new CouponCodeRequest(1L, 100L, "ABC123");
            UsedCouponResponse response = new UsedCouponResponse(1L, CouponStatus.USED);

            when(couponService.useMyCoupon(eq(1L), any(CouponCodeRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/coupon/my")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.couponId").value(1))
                    .andExpect(jsonPath("$.data.couponStatus").value("USED"))
                    .andDo(print())
                    .andDo(document("coupon-use",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("couponId").type(JsonFieldType.NUMBER).description("쿠폰 ID"),
                                    fieldWithPath("promotionId").type(JsonFieldType.NUMBER).description("프로모션 ID"),
                                    fieldWithPath("code").type(JsonFieldType.STRING).description("쿠폰 코드")
                            ),
                            responseFields(
                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                    fieldWithPath("data.couponId").type(JsonFieldType.NUMBER).description("쿠폰 ID"),
                                    fieldWithPath("data.couponStatus").type(JsonFieldType.STRING).description("쿠폰 상태 (USED)")
                            )
                    ));
        }
    }
}
