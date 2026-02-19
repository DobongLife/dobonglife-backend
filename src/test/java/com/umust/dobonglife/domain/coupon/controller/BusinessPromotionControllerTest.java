package com.umust.dobonglife.domain.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.umust.dobonglife.domain.coupon.controller.dto.request.PromotionRegisterRequest;
import com.umust.dobonglife.domain.coupon.controller.dto.request.PromotionUpdateRequest;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PresetResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionRegisterResponse;
import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionUpdateResponse;
import com.umust.dobonglife.domain.coupon.domain.constant.DiscountType;
import com.umust.dobonglife.domain.coupon.service.PreSetService;
import com.umust.dobonglife.domain.coupon.service.PromotionService;
import com.umust.dobonglife.global.common.model.constant.Category;
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
import org.springframework.mock.web.MockMultipartFile;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class BusinessPromotionControllerTest {

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

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Nested
    @DisplayName("POST /api/business/promotion/register")
    class RegisterCoupon {

        @Test
        @DisplayName("쿠폰 등록 성공")
        @WithMockCustomUser
        void 쿠폰_등록_성공() throws Exception {
            PromotionRegisterResponse response = new PromotionRegisterResponse(
                    1L, "여름 할인 쿠폰", "COUPON-ABC123", 100L,
                    "PERCENT", BigDecimal.valueOf(10),
                    LocalDate.of(2025, 7, 1), LocalDate.of(2025, 8, 31),
                    List.of("coupon-img1.jpg")
            );

            when(promotionService.registerCoupon(any(PromotionRegisterRequest.class), eq(1L), anyList()))
                    .thenReturn(response);

            PromotionRegisterRequest request = new PromotionRegisterRequest(
                    "여름 할인 쿠폰", "여름 시즌 특별 할인", 100L,
                    Category.RESTAURANT, DiscountType.PERCENT,
                    10, 10000, 5000, 100L, 30,
                    LocalDate.of(2025, 7, 1), LocalDate.of(2025, 8, 31)
            );
            String requestJson = objectMapper.writeValueAsString(request);

            MockMultipartFile requestPart = new MockMultipartFile(
                    "request", "", MediaType.APPLICATION_JSON_VALUE, requestJson.getBytes()
            );
            MockMultipartFile imagePart = new MockMultipartFile(
                    "imageFiles", "coupon.jpg", MediaType.IMAGE_JPEG_VALUE, "image-data".getBytes()
            );

            mockMvc.perform(multipart("/api/business/promotion/register")
                            .file(requestPart)
                            .file(imagePart)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.promotionId").value(1))
                    .andExpect(jsonPath("$.data.couponName").value("여름 할인 쿠폰"))
                    .andExpect(jsonPath("$.data.code").value("COUPON-ABC123"))
                    .andExpect(jsonPath("$.data.point").value(100))
                    .andExpect(jsonPath("$.data.discountType").value("PERCENT"))
                    .andDo(print())
                    .andDo(document("business-promotion-register",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestParts(
                                    partWithName("request").description("쿠폰 등록 요청 JSON"),
                                    partWithName("imageFiles").description("쿠폰 이미지 파일 목록 (선택)").optional()
                            ),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Business Promotion")
                                    .summary("사업장 쿠폰 등록")
                                    .description("사업장 프로모션 쿠폰을 등록합니다.")
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data.promotionId").type(JsonFieldType.NUMBER).description("프로모션 ID"),
                                            fieldWithPath("data.couponName").type(JsonFieldType.STRING).description("쿠폰 이름"),
                                            fieldWithPath("data.code").type(JsonFieldType.STRING).description("쿠폰 코드"),
                                            fieldWithPath("data.point").type(JsonFieldType.NUMBER).description("필요 포인트"),
                                            fieldWithPath("data.discountType").type(JsonFieldType.STRING).description("할인 유형 (PERCENT, AMOUNT)"),
                                            fieldWithPath("data.discountValue").type(JsonFieldType.NUMBER).description("할인 값"),
                                            fieldWithPath("data.startDate").type(JsonFieldType.STRING).description("시작일"),
                                            fieldWithPath("data.endDate").type(JsonFieldType.STRING).description("종료일"),
                                            fieldWithPath("data.imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록")
                                    )
                                    .build()
                            )
                    ));
        }

        @Test
        @DisplayName("이미지 없이 쿠폰 등록 성공")
        @WithMockCustomUser
        void 이미지_없이_쿠폰_등록_성공() throws Exception {
            PromotionRegisterResponse response = new PromotionRegisterResponse(
                    2L, "겨울 할인 쿠폰", "COUPON-XYZ789", 50L,
                    "AMOUNT", BigDecimal.valueOf(3000),
                    LocalDate.of(2025, 12, 1), LocalDate.of(2026, 1, 31),
                    List.of()
            );

            when(promotionService.registerCoupon(any(PromotionRegisterRequest.class), eq(1L), any()))
                    .thenReturn(response);

            PromotionRegisterRequest request = new PromotionRegisterRequest(
                    "겨울 할인 쿠폰", "겨울 시즌 할인", 50L,
                    Category.RESTAURANT, DiscountType.AMOUNT,
                    3000, 15000, 3000, 50L, 14,
                    LocalDate.of(2025, 12, 1), LocalDate.of(2026, 1, 31)
            );
            String requestJson = objectMapper.writeValueAsString(request);

            MockMultipartFile requestPart = new MockMultipartFile(
                    "request", "", MediaType.APPLICATION_JSON_VALUE, requestJson.getBytes()
            );

            mockMvc.perform(multipart("/api/business/promotion/register")
                            .file(requestPart)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.promotionId").value(2))
                    .andExpect(jsonPath("$.data.imageUrls").isEmpty());
        }
    }

    @Nested
    @DisplayName("PATCH /api/business/promotion/update/{promotionId}")
    class UpdateCoupon {

        @Test
        @DisplayName("쿠폰 수정 성공")
        @WithMockCustomUser
        void 쿠폰_수정_성공() throws Exception {
            PromotionUpdateResponse response = new PromotionUpdateResponse(
                    "수정된 쿠폰명", "수정된 설명", 200L
            );

            when(promotionService.updateCoupon(any(PromotionUpdateRequest.class), eq(1L), eq(1L)))
                    .thenReturn(response);

            PromotionUpdateRequest request = new PromotionUpdateRequest(
                    "수정된 쿠폰명", "수정된 설명", 200L
            );

            mockMvc.perform(patch("/api/business/promotion/update/{promotionId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.couponName").value("수정된 쿠폰명"))
                    .andExpect(jsonPath("$.data.couponDescription").value("수정된 설명"))
                    .andExpect(jsonPath("$.data.totalQuantity").value(200))
                    .andDo(print())
                    .andDo(document("business-promotion-update",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Business Promotion")
                                    .summary("사업장 쿠폰 수정")
                                    .description("사업장 프로모션 쿠폰을 수정합니다.")
                                    .pathParameters(
                                            parameterWithName("promotionId").description("수정할 프로모션 ID")
                                    )
                                    .requestFields(
                                            fieldWithPath("couponName").type(JsonFieldType.STRING).description("쿠폰 이름"),
                                            fieldWithPath("couponDescription").type(JsonFieldType.STRING).description("쿠폰 설명"),
                                            fieldWithPath("totalQuantity").type(JsonFieldType.NUMBER).description("총 수량")
                                    )
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data.couponName").type(JsonFieldType.STRING).description("쿠폰 이름"),
                                            fieldWithPath("data.couponDescription").type(JsonFieldType.STRING).description("쿠폰 설명"),
                                            fieldWithPath("data.totalQuantity").type(JsonFieldType.NUMBER).description("총 수량")
                                    )
                                    .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("GET /api/business/promotion/preset")
    class GetPreset {

        @Test
        @DisplayName("프리셋 조회 성공")
        @WithMockCustomUser
        void 프리셋_조회_성공() throws Exception {
            PresetResponse response = new PresetResponse(
                    1L, "RESTAURANT", "음식점 할인 쿠폰 프리셋", 100L,
                    "preset-img.jpg", "PERCENT", 10,
                    10000L, 5000L, 30L,
                    LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 31)
            );

            when(preSetService.getPreset(eq(1L)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/business/promotion/preset"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.category").value("RESTAURANT"))
                    .andExpect(jsonPath("$.data.couponDescription").value("음식점 할인 쿠폰 프리셋"))
                    .andExpect(jsonPath("$.data.point").value(100))
                    .andExpect(jsonPath("$.data.discountType").value("PERCENT"))
                    .andExpect(jsonPath("$.data.discountValue").value(10))
                    .andDo(print())
                    .andDo(document("business-promotion-preset",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Business Promotion")
                                    .summary("프리셋 조회")
                                    .description("쿠폰 등록용 프리셋을 조회합니다.")
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("프리셋 ID"),
                                            fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                                            fieldWithPath("data.couponDescription").type(JsonFieldType.STRING).description("쿠폰 설명"),
                                            fieldWithPath("data.point").type(JsonFieldType.NUMBER).description("필요 포인트"),
                                            fieldWithPath("data.img").type(JsonFieldType.STRING).description("프리셋 이미지 URL"),
                                            fieldWithPath("data.discountType").type(JsonFieldType.STRING).description("할인 유형 (PERCENT, AMOUNT)"),
                                            fieldWithPath("data.discountValue").type(JsonFieldType.NUMBER).description("할인 값"),
                                            fieldWithPath("data.minPurchaseAmount").type(JsonFieldType.NUMBER).description("최소 구매 금액"),
                                            fieldWithPath("data.maxDiscountAmount").type(JsonFieldType.NUMBER).description("최대 할인 금액"),
                                            fieldWithPath("data.validityDays").type(JsonFieldType.NUMBER).description("유효 기간 (일)"),
                                            fieldWithPath("data.issueStartDate").type(JsonFieldType.STRING).description("발행 시작일"),
                                            fieldWithPath("data.issueEndDate").type(JsonFieldType.STRING).description("발행 종료일")
                                    )
                                    .build()
                            )
                    ));
        }
    }
}
