package com.umust.dobonglife.domain.business.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.application.business.service.BusinessFacade;
import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.entity.PlaceDetail;
import com.umust.dobonglife.domain.place.domain.vo.Theme;
import com.umust.dobonglife.domain.promotion.domain.constant.DiscountType;
import com.umust.dobonglife.domain.promotion.domain.entity.Promotion;
import com.umust.dobonglife.global.common.constant.Category;
import com.umust.dobonglife.infra.firebase.FirebaseConfig;
import com.umust.dobonglife.global.support.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.SimpleType.INTEGER;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
public class BusinessControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    BusinessFacade businessFacade;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    private Business createBusiness(Long id, Long userId, Long placeId) {
        Business business = Business.builder()
                .userId(userId)
                .placeId(placeId)
                .category(Category.CAFE)
                .businessNumber("2198701322")
                .managerName("홍길동")
                .email("biz@example.com")
                .build();
        ReflectionTestUtils.setField(business, "id", id);
        return business;
    }

    private Place createPlace(Long id) {
        Place place = Place.builder()
                .category(Category.CAFE)
                .name("도봉 카페")
                .latitude(37.6898)
                .longitude(127.0472)
                .build();
        ReflectionTestUtils.setField(place, "id", id);

        PlaceDetail detail = PlaceDetail.builder()
                .subName("자연 속 카페")
                .content("아름다운 카페입니다")
                .address("서울 도봉구 도봉로 123")
                .contact("02-123-4567")
                .operatingHour("09:00~18:00")
                .build();
        place.attachDetail(detail);
        place.attachImages(List.of("https://example.com/img1.jpg"));
        place.attachThemes(List.of(Theme.NATURE));

        return place;
    }

    // =========================================================================
    // POST /api/business - 사업장 등록
    // =========================================================================
    @Test
    @DisplayName("사업장 등록 - 성공")
    @WithMockCustomUser
    void registerBusiness_success() throws Exception {
        willDoNothing().given(businessFacade).registerBusiness(any(), any(), any());

        String requestJson = """
                {
                    "businessName": "도봉 카페",
                    "businessAddress": "서울 도봉구 도봉로 123",
                    "content": "아름다운 카페입니다",
                    "contact": "02-123-4567",
                    "email": "biz@example.com",
                    "operatingHour": "09:00~18:00",
                    "managerName": "홍길동",
                    "businessNumber": "2198701322",
                    "category": "CAFE",
                    "themes": ["NATURE"],
                    "latitude": 37.6898,
                    "longitude": 127.0472
                }
                """;

        MockMultipartFile requestPart = new MockMultipartFile(
                "request", "", MediaType.APPLICATION_JSON_VALUE, requestJson.getBytes());
        MockMultipartFile imagePart = new MockMultipartFile(
                "imageFiles", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "image-data".getBytes());

        mockMvc.perform(multipart("/api/business")
                        .file(requestPart)
                        .file(imagePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("business-register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("request").description("사업장 등록 요청 JSON"),
                                partWithName("imageFiles").description("사업장 이미지 파일 목록").optional()
                        ),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("사업자 API")
                                        .summary("사업장 등록")
                                        .description("카테고리는 다음과 같습니다. RESTAURANT, CAFE, SHOPPING, MEDICAL_IT, BEAUTY, FITNESS, EXPERIENCE, ETC")
                                        .responseFields(
                                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)")
                                        )
                                        .build()
                        )
                ));
    }

    // =========================================================================
    // GET /api/business - 사업장 정보 조회
    // =========================================================================
    @Test
    @DisplayName("사업장 정보 조회 - 성공")
    @WithMockCustomUser
    void getBusiness_success() throws Exception {
        Business business = createBusiness(1L, 1L, 10L);
        Place place = createPlace(10L);
        BusinessFacade.BusinessInfo info = new BusinessFacade.BusinessInfo(business, place);

        given(businessFacade.getBusinessInfo(eq(1L))).willReturn(info);

        mockMvc.perform(get("/api/business")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.businessId").value(1))
                .andExpect(jsonPath("$.data.placeName").value("도봉 카페"))
                .andExpect(jsonPath("$.data.email").value("biz@example.com"))
                .andExpect(jsonPath("$.data.managerName").value("홍길동"))
                .andExpect(jsonPath("$.data.businessNumber").value("2198701322"))
                .andDo(print())
                .andDo(document("business-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("사업자 API")
                                        .summary("사업장 정보 조회")
                                        .description("사업장 정보를 조회합니다.")
                                        .responseFields(
                                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("data.businessId").type(JsonFieldType.NUMBER).description("사업장 ID"),
                                                fieldWithPath("data.businessNumber").type(JsonFieldType.STRING).description("사업자등록번호"),
                                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("data.managerName").type(JsonFieldType.STRING).description("대표자 이름"),
                                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                                fieldWithPath("data.placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                                fieldWithPath("data.placeName").type(JsonFieldType.STRING).description("장소명"),
                                                fieldWithPath("data.subName").type(JsonFieldType.STRING).description("장소 부제"),
                                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("사업장 소개"),
                                                fieldWithPath("data.address").type(JsonFieldType.STRING).description("주소"),
                                                fieldWithPath("data.contact").type(JsonFieldType.STRING).description("전화번호"),
                                                fieldWithPath("data.operatingHour").type(JsonFieldType.STRING).description("운영 시간"),
                                                fieldWithPath("data.latitude").type(JsonFieldType.NUMBER).description("위도"),
                                                fieldWithPath("data.longitude").type(JsonFieldType.NUMBER).description("경도"),
                                                fieldWithPath("data.thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                                                fieldWithPath("data.imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                                fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                                                fieldWithPath("data.themes").type(JsonFieldType.ARRAY).description("테마 목록")
                                        )
                                        .build()
                        )
                ));
    }

    // =========================================================================
    // PATCH /api/business - 사업장 수정
    // =========================================================================
    @Test
    @DisplayName("사업장 수정 - 성공")
    @WithMockCustomUser
    void updateBusiness_success() throws Exception {
        Business business = createBusiness(1L, 1L, 10L);
        ReflectionTestUtils.setField(business, "email", "updated@example.com");
        ReflectionTestUtils.setField(business, "managerName", "이강파");
        Place place = createPlace(10L);
        BusinessFacade.BusinessInfo info = new BusinessFacade.BusinessInfo(business, place);

        given(businessFacade.updateBusiness(eq(1L), any(), any())).willReturn(info);

        String requestJson = """
                {
                    "businessName": "(주)유머스트알엔디",
                    "subName": "도봉구 최고의 명소입니다.",
                    "content": "서울특별시 도봉구 마들로",
                    "contact": "02-123-4567",
                    "email": "updated@example.com",
                    "operatingHour": "평일 09:00 ~ 18:00",
                    "managerName": "이강파",
                    "category": "CAFE"
                }
                """;

        MockMultipartFile requestPart = new MockMultipartFile(
                "request", "", MediaType.APPLICATION_JSON_VALUE, requestJson.getBytes());
        MockMultipartFile imagePart = new MockMultipartFile(
                "imageFiles", "updated.jpg", MediaType.IMAGE_JPEG_VALUE, "image-data".getBytes());

        mockMvc.perform(multipart("/api/business")
                        .file(requestPart)
                        .file(imagePart)
                        .with(request -> { request.setMethod("PATCH"); return request; })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.businessId").value(1))
                .andExpect(jsonPath("$.data.email").value("updated@example.com"))
                .andExpect(jsonPath("$.data.managerName").value("이강파"))
                .andDo(print())
                .andDo(document("business-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("request").description("사업장 수정 요청 JSON"),
                                partWithName("imageFiles").description("사업장 이미지 파일 목록").optional()
                        ),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("사업자 API")
                                        .summary("사업장 수정")
                                        .description("사업장 정보를 수정합니다.")
                                        .responseFields(
                                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("data.businessId").type(JsonFieldType.NUMBER).description("사업장 ID"),
                                                fieldWithPath("data.businessNumber").type(JsonFieldType.STRING).description("사업자등록번호"),
                                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("data.managerName").type(JsonFieldType.STRING).description("대표자 이름"),
                                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                                fieldWithPath("data.placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                                fieldWithPath("data.placeName").type(JsonFieldType.STRING).description("장소명"),
                                                fieldWithPath("data.subName").type(JsonFieldType.STRING).description("장소 부제"),
                                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("사업장 소개"),
                                                fieldWithPath("data.address").type(JsonFieldType.STRING).description("주소"),
                                                fieldWithPath("data.contact").type(JsonFieldType.STRING).description("전화번호"),
                                                fieldWithPath("data.operatingHour").type(JsonFieldType.STRING).description("운영 시간"),
                                                fieldWithPath("data.latitude").type(JsonFieldType.NUMBER).description("위도"),
                                                fieldWithPath("data.longitude").type(JsonFieldType.NUMBER).description("경도"),
                                                fieldWithPath("data.thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                                                fieldWithPath("data.imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                                fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                                                fieldWithPath("data.themes").type(JsonFieldType.ARRAY).description("테마 목록")
                                        )
                                        .build()
                        )
                ));
    }

    // =========================================================================
    // GET /api/business/promotion - 사업장 프로모션 조회
    // =========================================================================
    @Test
    @DisplayName("사업장 프로모션 조회 - 성공")
    @WithMockCustomUser
    void getBusinessPromotion_success() throws Exception {
        Promotion promotion = Promotion.builder()
                .businessId(1L)
                .category(Category.CAFE)
                .title("여름 할인 이벤트")
                .description("여름 맞이 10% 할인")
                .startDate(LocalDate.of(2025, 6, 1))
                .endDate(LocalDate.of(2027, 8, 31))
                .couponValidDays(30)
                .discountType(DiscountType.PERCENT)
                .discountValue(10L)
                .minPrice(0L)
                .maxPrice(100000L)
                .point(0L)
                .totalQuantity(100L)
                .build();
        ReflectionTestUtils.setField(promotion, "id", 1L);

        BusinessFacade.PromotionPage page = new BusinessFacade.PromotionPage(
                List.of(promotion),
                Map.of(1L, 50L),
                false
        );

        given(businessFacade.getBusinessPromotions(eq(1L), isNull(), eq(3))).willReturn(page);

        mockMvc.perform(get("/api/business/promotion")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].promotionId").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("여름 할인 이벤트"))
                .andExpect(jsonPath("$.data.content[0].usedCount").value(50))
                .andExpect(jsonPath("$.data.content[0].totalCount").value(100))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andDo(print())
                .andDo(document("business-promotion-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("사업자 API")
                                        .summary("사업장 프로모션 목록 조회")
                                        .description("사업장의 프로모션 목록을 조회합니다.")
                                        .queryParameters(
                                                parameterWithName("lastId").optional().description("커서 - 마지막 프로모션 ID (첫 요청 시 생략)").type(INTEGER),
                                                parameterWithName("size").optional().description("조회 개수 (기본값: 3)").type(INTEGER)
                                        )
                                        .responseFields(
                                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("프로모션 목록"),
                                                fieldWithPath("data.content[].promotionId").type(JsonFieldType.NUMBER).description("프로모션 ID"),
                                                fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("프로모션 제목"),
                                                fieldWithPath("data.content[].startDate").type(JsonFieldType.STRING).description("시작일"),
                                                fieldWithPath("data.content[].endDate").type(JsonFieldType.STRING).description("종료일"),
                                                fieldWithPath("data.content[].inPeriod").type(JsonFieldType.BOOLEAN).description("진행 중 여부"),
                                                fieldWithPath("data.content[].discountType").type(JsonFieldType.STRING).description("할인 유형 (PERCENT, AMOUNT)"),
                                                fieldWithPath("data.content[].discountValue").type(JsonFieldType.NUMBER).description("할인 값"),
                                                fieldWithPath("data.content[].usedValue").type(JsonFieldType.NUMBER).description("사용률 (%)"),
                                                fieldWithPath("data.content[].usedCount").type(JsonFieldType.NUMBER).description("사용 횟수"),
                                                fieldWithPath("data.content[].totalCount").type(JsonFieldType.NUMBER).description("총 발급 수"),
                                                fieldWithPath("data.content[].code").type(JsonFieldType.STRING).description("쿠폰 코드"),
                                                fieldWithPath("data.content[].description").type(JsonFieldType.STRING).description("프로모션 설명"),
                                                fieldWithPath("data.content[].validPeriod").type(JsonFieldType.NUMBER).description("유효 기간 (일)"),
                                                fieldWithPath("data.lastId").type(JsonFieldType.NUMBER).description("마지막 프로모션 ID"),
                                                fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                                        )
                                        .build()
                        )
                ));
    }
}
