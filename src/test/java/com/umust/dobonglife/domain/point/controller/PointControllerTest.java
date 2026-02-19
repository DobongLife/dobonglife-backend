package com.umust.dobonglife.domain.point.controller;

import com.umust.dobonglife.domain.coupon.controller.dto.response.PromotionBannerItem;
import com.umust.dobonglife.domain.point.controller.dto.response.MyPointsResponse;
import com.umust.dobonglife.domain.point.controller.dto.response.PointGuideResponse;
import com.umust.dobonglife.domain.point.controller.dto.response.PointPageResponse;
import com.umust.dobonglife.domain.point.controller.dto.response.PointResponse;
import com.umust.dobonglife.domain.point.service.PointPromotionService;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.slice.SliceResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;

import com.epages.restdocs.apispec.ResourceSnippetParameters;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class PointControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PointPromotionService pointPromotionService;

    @MockitoBean
    PointService pointService;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Nested
    @DisplayName("GET /api/points")
    class GetMyPoint {

        @Test
        @DisplayName("포인트 첫화면 조회 성공")
        @WithMockCustomUser
        void 포인트_첫화면_조회_성공() throws Exception {
            PromotionBannerItem banner = new PromotionBannerItem(
                    1L, "음식", "할인 이벤트", "설명", List.of("img1.jpg")
            );
            CursorResponse<PromotionBannerItem> promotions =
                    new CursorResponse<>(List.of(banner), false);
            PointPageResponse response = new PointPageResponse(500L, promotions);

            when(pointPromotionService.getMyPoint(eq(1L), isNull(), eq(4)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/points"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.totalPoint").value(500))
                    .andExpect(jsonPath("$.data.promotionList.content[0].promotionId").value(1))
                    .andExpect(jsonPath("$.data.promotionList.content[0].title").value("할인 이벤트"))
                    .andDo(print())
                    .andDo(document("point-main",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("포인트 API")
                                            .summary("쿠폰 첫화면 조회")
                                            .description("포인트와 프로모션(광고)를 조회합니다.")
                                            .queryParameters(
                                                    parameterWithName("lastId").optional()
                                                            .description("커서 - 마지막 프로모션 ID (첫 요청 시 생략)"),
                                                    parameterWithName("size").optional()
                                                            .description("조회 개수 (기본값: 4)")
                                            )
                                            .responseFields(
                                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                    fieldWithPath("data.totalPoint").type(JsonFieldType.NUMBER).description("총 포인트"),
                                                    fieldWithPath("data.promotionList.content[]").type(JsonFieldType.ARRAY).description("프로모션 배너 목록"),
                                                    fieldWithPath("data.promotionList.content[].promotionId").type(JsonFieldType.NUMBER).description("프로모션 ID"),
                                                    fieldWithPath("data.promotionList.content[].category").type(JsonFieldType.STRING).description("카테고리"),
                                                    fieldWithPath("data.promotionList.content[].title").type(JsonFieldType.STRING).description("프로모션 제목"),
                                                    fieldWithPath("data.promotionList.content[].description").type(JsonFieldType.STRING).description("프로모션 설명"),
                                                    fieldWithPath("data.promotionList.content[].imgUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                                    fieldWithPath("data.promotionList.lastId").type(JsonFieldType.NUMBER).description("마지막 프로모션 ID"),
                                                    fieldWithPath("data.promotionList.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                                            )
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("lastId 파라미터 전달 성공")
        @WithMockCustomUser
        void lastId_파라미터_전달_성공() throws Exception {
            CursorResponse<PromotionBannerItem> promotions =
                    new CursorResponse<>(List.of(), false);
            PointPageResponse response = new PointPageResponse(300L, promotions);

            when(pointPromotionService.getMyPoint(eq(1L), eq(5L), eq(4)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/points").param("lastId", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalPoint").value(300));
        }

        @Test
        @DisplayName("빈 프로모션 리스트 성공")
        @WithMockCustomUser
        void 빈_프로모션_리스트_성공() throws Exception {
            CursorResponse<PromotionBannerItem> promotions =
                    new CursorResponse<>(List.of(), false);
            PointPageResponse response = new PointPageResponse(0L, promotions);

            when(pointPromotionService.getMyPoint(eq(1L), isNull(), eq(4)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/points"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalPoint").value(0))
                    .andExpect(jsonPath("$.data.promotionList.content").isEmpty());
        }

        @Test
        @DisplayName("사용자 없으면 에러 응답")
        @WithMockCustomUser
        void 사용자_없으면_에러_응답() throws Exception {
            when(pointPromotionService.getMyPoint(eq(1L), isNull(), eq(4)))
                    .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

            mockMvc.perform(get("/api/points"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(ErrorCode.USER_NOT_FOUND.getCode()));
        }
    }

    @Nested
    @DisplayName("GET /api/points/my")
    class GetMyPointList {

        @Test
        @DisplayName("포인트 내역 조회 성공")
        @WithMockCustomUser
        void 포인트_내역_조회_성공() throws Exception {
            PointResponse pr = PointResponse.builder()
                    .pointId(1L)
                    .title("후기 작성")
                    .amount(10L)
                    .createAt(LocalDateTime.of(2025, 1, 1, 12, 0))
                    .afterBalance(510L)
                    .isUsed(false)
                    .build();
            SliceResponse<PointResponse> slice = SliceResponse.<PointResponse>builder()
                    .content(List.of(pr))
                    .size(20)
                    .hasNext(false)
                    .nextCursor(null)
                    .build();
            List<PointGuideResponse> guides = List.of(
                    new PointGuideResponse("후기 작성", 10L),
                    new PointGuideResponse("코스 등록", 30L)
            );
            MyPointsResponse response = new MyPointsResponse(500L, guides, slice);

            when(pointService.getPointList(eq(1L), eq(20), isNull(), eq("DESC")))
                    .thenReturn(response);

            mockMvc.perform(get("/api/points/my"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalPoint").value(500))
                    .andExpect(jsonPath("$.data.pointGuides[0].title").value("후기 작성"))
                    .andExpect(jsonPath("$.data.pointGuides[0].rewardPoint").value(10))
                    .andExpect(jsonPath("$.data.pointGuides[1].title").value("코스 등록"))
                    .andExpect(jsonPath("$.data.pointGuides[1].rewardPoint").value(30))
                    .andExpect(jsonPath("$.data.pointList.content[0].pointId").value(1))
                    .andExpect(jsonPath("$.data.pointList.content[0].title").value("후기 작성"))
                    .andExpect(jsonPath("$.data.pointList.content[0].amount").value(10))
                    .andDo(print())
                    .andDo(document("point-my-list",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("포인트 API")
                                            .summary("포인트 내역 조회")
                                            .description("포인트 내역과 포인트 가이드를 조회합니다.")
                                            .queryParameters(
                                                    parameterWithName("size").optional()
                                                            .description("조회 개수 (기본값: 20)"),
                                                    parameterWithName("lastId").optional()
                                                            .description("커서 - 마지막 포인트 ID (첫 요청 시 생략)"),
                                                    parameterWithName("order").optional()
                                                            .description("정렬 순서 (DESC/ASC, 기본값: DESC)")
                                            )
                                            .responseFields(
                                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                    fieldWithPath("data.totalPoint").type(JsonFieldType.NUMBER).description("총 포인트"),
                                                    fieldWithPath("data.pointGuides[]").type(JsonFieldType.ARRAY).description("포인트 가이드 목록"),
                                                    fieldWithPath("data.pointGuides[].title").type(JsonFieldType.STRING).description("가이드 제목"),
                                                    fieldWithPath("data.pointGuides[].rewardPoint").type(JsonFieldType.NUMBER).description("보상 포인트"),
                                                    fieldWithPath("data.pointList.content[]").type(JsonFieldType.ARRAY).description("포인트 내역 목록"),
                                                    fieldWithPath("data.pointList.content[].pointId").type(JsonFieldType.NUMBER).description("포인트 ID"),
                                                    fieldWithPath("data.pointList.content[].title").type(JsonFieldType.STRING).description("포인트 사유"),
                                                    fieldWithPath("data.pointList.content[].amount").type(JsonFieldType.NUMBER).description("포인트 금액"),
                                                    fieldWithPath("data.pointList.content[].createAt").type(JsonFieldType.STRING).description("생성 일시"),
                                                    fieldWithPath("data.pointList.content[].afterBalance").type(JsonFieldType.NUMBER).description("적립/사용 후 잔액"),
                                                    fieldWithPath("data.pointList.content[].used").type(JsonFieldType.BOOLEAN).description("사용 여부"),
                                                    fieldWithPath("data.pointList.size").type(JsonFieldType.NUMBER).description("조회 크기"),
                                                    fieldWithPath("data.pointList.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부"),
                                                    fieldWithPath("data.pointList.nextCursor").type(JsonFieldType.NULL).description("다음 커서 값").optional()
                                            )
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("커스텀 파라미터 전달 성공")
        @WithMockCustomUser
        void 커스텀_파라미터_전달_성공() throws Exception {
            SliceResponse<PointResponse> slice = SliceResponse.<PointResponse>builder()
                    .content(List.of())
                    .size(10)
                    .hasNext(false)
                    .nextCursor(null)
                    .build();
            MyPointsResponse response = new MyPointsResponse(0L, List.of(), slice);

            when(pointService.getPointList(eq(1L), eq(10), eq(5L), eq("ASC")))
                    .thenReturn(response);

            mockMvc.perform(get("/api/points/my")
                            .param("size", "10")
                            .param("lastId", "5")
                            .param("order", "ASC"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalPoint").value(0));
        }

        @Test
        @DisplayName("페이지네이션 hasNext 검증")
        @WithMockCustomUser
        void 페이지네이션_hasNext_검증() throws Exception {
            PointResponse pr = PointResponse.builder()
                    .pointId(10L)
                    .title("코스 등록")
                    .amount(30L)
                    .createAt(LocalDateTime.of(2025, 1, 1, 12, 0))
                    .afterBalance(300L)
                    .isUsed(false)
                    .build();
            SliceResponse<PointResponse> slice = SliceResponse.<PointResponse>builder()
                    .content(List.of(pr))
                    .size(20)
                    .hasNext(true)
                    .nextCursor("10")
                    .build();
            MyPointsResponse response = new MyPointsResponse(300L, List.of(), slice);

            when(pointService.getPointList(eq(1L), eq(20), isNull(), eq("DESC")))
                    .thenReturn(response);

            mockMvc.perform(get("/api/points/my"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.pointList.hasNext").value(true))
                    .andExpect(jsonPath("$.data.pointList.nextCursor").value("10"));
        }

        @Test
        @DisplayName("사용자 없으면 에러 응답")
        @WithMockCustomUser
        void 사용자_없으면_에러_응답() throws Exception {
            when(pointService.getPointList(eq(1L), eq(20), isNull(), eq("DESC")))
                    .thenThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

            mockMvc.perform(get("/api/points/my"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(ErrorCode.USER_NOT_FOUND.getCode()));
        }
    }
}
