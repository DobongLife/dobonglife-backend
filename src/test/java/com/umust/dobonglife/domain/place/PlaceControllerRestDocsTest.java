package com.umust.dobonglife.domain.place;

import com.umust.dobonglife.domain.place.controller.dto.response.PlaceDetailResponse;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceSummaryListResponse;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceSummaryResponse;
import com.umust.dobonglife.domain.place.service.PlaceReviewService;
import com.umust.dobonglife.domain.place.service.PlaceService;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewSummaryResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.external.firebase.FirebaseConfig;
import com.umust.dobonglife.global.support.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class PlaceControllerRestDocsTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PlaceService placeService;

    @MockitoBean
    PlaceReviewService placeReviewService;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    // =========================================================================
    // POST /api/places/{placeId}/like
    // =========================================================================
    @Test
    @DisplayName("장소 좋아요 API - 성공")
    @WithMockCustomUser
    void likePlace_success() throws Exception {
        Long placeId = 1L;

        doNothing().when(placeService).toggleLikes(eq(1L), eq(placeId));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/places/{placeId}/like", placeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("place-like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("Place")
                                .summary("장소 좋아요")
                                .description("장소를 좋아요하거나 해제합니다.")
                                .pathParameters(
                                        parameterWithName("placeId").description("장소 고유 ID")
                                )
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)")
                                )
                                .build()
                        )
                ));
    }

    // =========================================================================
    // GET /api/places/like/my
    // =========================================================================
    @Test
    @DisplayName("좋아요 장소 조회 API - 성공")
    @WithMockCustomUser
    void getMyLikedPlace_success() throws Exception {
        List<PlaceSummaryResponse> places = List.of(
                PlaceSummaryResponse.builder()
                        .placeId(1L)
                        .placeName("도봉산 카페")
                        .category("카페")
                        .thumbnailUrl("https://example.com/thumb1.jpg")
                        .averageRating(4.5)
                        .reviewCount(10L)
                        .isLiked(true)
                        .latitude(37.6898)
                        .longitude(127.0472)
                        .themes(List.of("NATURE"))
                        .build()
        );

        CursorResponse<PlaceSummaryResponse> cursorResponse =
                new CursorResponse<>(places, 1L, false);

        given(placeService.getLikedPlace(eq(1L), eq(2), isNull()))
                .willReturn(cursorResponse);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/places/like/my")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].placeId").value(1))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andDo(print())
                .andDo(document("place-liked-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("Place")
                                .summary("찜한 장소 목록 조회")
                                .description("내가 찜한 장소 목록을 조회합니다.")
                                .queryParameters(
                                        parameterWithName("lastId").optional()
                                                .description("커서 - 마지막 장소 ID (첫 요청 시 생략)"),
                                        parameterWithName("size").optional()
                                                .description("조회 개수 (기본값: 2)")
                                )
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("장소 목록"),
                                        fieldWithPath("data.content[].placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                        fieldWithPath("data.content[].placeName").type(JsonFieldType.STRING).description("장소명"),
                                        fieldWithPath("data.content[].category").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("data.content[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                                        fieldWithPath("data.content[].averageRating").type(JsonFieldType.NUMBER).description("평균 평점"),
                                        fieldWithPath("data.content[].reviewCount").type(JsonFieldType.NUMBER).description("리뷰 수"),
                                        fieldWithPath("data.content[].liked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                        fieldWithPath("data.content[].latitude").type(JsonFieldType.NUMBER).description("위도"),
                                        fieldWithPath("data.content[].longitude").type(JsonFieldType.NUMBER).description("경도"),
                                        fieldWithPath("data.content[].themes").type(JsonFieldType.ARRAY).description("테마 목록"),
                                        fieldWithPath("data.lastId").type(JsonFieldType.NUMBER).description("마지막 장소 ID"),
                                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                                )
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("좋아요 장소 조회 API - 커서 페이징 (더보기)")
    @WithMockCustomUser
    void getMyLikedPlace_withCursor() throws Exception {
        Long lastId = 5L;

        List<PlaceSummaryResponse> places = List.of(
                PlaceSummaryResponse.builder()
                        .placeId(3L)
                        .placeName("도봉 맛집")
                        .category("음식점")
                        .thumbnailUrl("https://example.com/thumb2.jpg")
                        .averageRating(4.0)
                        .reviewCount(5L)
                        .isLiked(true)
                        .latitude(37.6800)
                        .longitude(127.0400)
                        .themes(List.of("RESTAURANT"))
                        .build()
        );

        CursorResponse<PlaceSummaryResponse> cursorResponse =
                new CursorResponse<>(places, 3L, true);

        given(placeService.getLikedPlace(eq(1L), eq(2), eq(lastId)))
                .willReturn(cursorResponse);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/places/like/my")
                        .param("lastId", String.valueOf(lastId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasNext").value(true))
                .andDo(print())
                .andDo(document("place-liked-list-cursor",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("Place")
                                .summary("찜한 장소 목록 커서 조회")
                                .description("커서 기반으로 찜한 장소 목록을 조회합니다.")
                                .queryParameters(
                                        parameterWithName("lastId")
                                                .description("이전 페이지에서 받은 마지막 장소 ID (커서)")
                                )
                                .build()
                        )
                ));
    }

    // =========================================================================
    // GET /api/places/{placeId}
    // =========================================================================
    @Test
    @DisplayName("장소 상세 조회 API - 성공")
    @WithMockCustomUser
    void getPlaceDetail_success() throws Exception {
        Long placeId = 1L;

        List<ReviewSummaryResponse> reviewList = List.of(
                new ReviewSummaryResponse(10L, "리뷰어A", 5.0, "정말 좋은 장소입니다!",
                        List.of("https://example.com/review1.jpg"),
                        LocalDateTime.of(2025, 6, 1, 10, 0), false),
                new ReviewSummaryResponse(9L, "리뷰어B", 4.0, "분위기가 좋아요",
                        List.of(),
                        LocalDateTime.of(2025, 5, 28, 14, 30), false)
        );

        CursorResponse<ReviewSummaryResponse> cursorReviews =
                new CursorResponse<>(reviewList, 9L, true);

        PlaceDetailResponse response = PlaceDetailResponse.builder()
                .placeId(1L)
                .name("도봉산 카페")
                .subName("자연 속 카페")
                .category("카페")
                .content("아름다운 자연 속에서 커피를 즐길 수 있는 카페입니다.")
                .address("서울 도봉구 도봉산길 123")
                .placeImages(List.of("https://example.com/img1.jpg", "https://example.com/img2.jpg"))
                .operatingHour("09:00 ~ 21:00")
                .contact("02-123-4567")
                .themes(List.of("NATURE", "CULTURE"))
                .averageRating(4.5)
                .reviewCount(15L)
                .latitude(37.6898)
                .longitude(127.0472)
                .isLiked(true)
                .reviews(cursorReviews)
                .build();

        given(placeReviewService.getPlaceDetail(eq(placeId), eq(1L), isNull(), eq(2)))
                .willReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/places/{placeId}", placeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.placeId").value(1))
                .andExpect(jsonPath("$.data.name").value("도봉산 카페"))
                .andExpect(jsonPath("$.data.liked").value(true))
                .andDo(print())
                .andDo(document("place-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("Place")
                                .summary("장소 상세 조회")
                                .description("장소 상세 정보와 리뷰를 조회합니다.")
                                .pathParameters(
                                        parameterWithName("placeId").description("장소 고유 ID")
                                )
                                .queryParameters(
                                        parameterWithName("lastId").optional()
                                                .description("커서 - 마지막 리뷰 ID (첫 요청 시 생략)"),
                                        parameterWithName("size").optional()
                                                .description("리뷰 조회 개수 (기본값: 2)")
                                )
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("장소명"),
                                        fieldWithPath("data.subName").type(JsonFieldType.STRING).description("장소 부제"),
                                        fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("장소 설명"),
                                        fieldWithPath("data.address").type(JsonFieldType.STRING).description("주소"),
                                        fieldWithPath("data.placeImages").type(JsonFieldType.ARRAY).description("장소 이미지 URL 목록"),
                                        fieldWithPath("data.operatingHour").type(JsonFieldType.STRING).description("운영 시간"),
                                        fieldWithPath("data.contact").type(JsonFieldType.STRING).description("연락처"),
                                        fieldWithPath("data.themes").type(JsonFieldType.ARRAY).description("테마 목록"),
                                        fieldWithPath("data.averageRating").type(JsonFieldType.NUMBER).description("평균 평점"),
                                        fieldWithPath("data.reviewCount").type(JsonFieldType.NUMBER).description("리뷰 수"),
                                        fieldWithPath("data.latitude").type(JsonFieldType.NUMBER).description("위도"),
                                        fieldWithPath("data.longitude").type(JsonFieldType.NUMBER).description("경도"),
                                        fieldWithPath("data.liked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                        fieldWithPath("data.reviews").type(JsonFieldType.OBJECT).description("리뷰 커서 페이징 응답"),
                                        fieldWithPath("data.reviews.content[]").type(JsonFieldType.ARRAY).description("리뷰 목록"),
                                        fieldWithPath("data.reviews.content[].reviewId").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                        fieldWithPath("data.reviews.content[].name").type(JsonFieldType.STRING).description("작성자 이름"),
                                        fieldWithPath("data.reviews.content[].rating").type(JsonFieldType.NUMBER).description("평점"),
                                        fieldWithPath("data.reviews.content[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                        fieldWithPath("data.reviews.content[].imageUrls").type(JsonFieldType.ARRAY).description("리뷰 이미지 URL"),
                                        fieldWithPath("data.reviews.content[].updatedAt").type(JsonFieldType.STRING).description("수정 일시"),
                                        fieldWithPath("data.reviews.content[].owner").type(JsonFieldType.BOOLEAN).description("현재 사용자 작성 여부"),
                                        fieldWithPath("data.reviews.lastId").type(JsonFieldType.NUMBER).description("마지막 리뷰 ID"),
                                        fieldWithPath("data.reviews.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                                )
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("장소 상세 조회 API - 존재하지 않는 장소")
    @WithMockCustomUser
    void getPlaceDetail_notFound() throws Exception {
        Long placeId = 999L;

        given(placeReviewService.getPlaceDetail(eq(placeId), eq(1L), isNull(), eq(2)))
                .willThrow(new BusinessException(ErrorCode.PLACE_NOT_FOUND));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/places/{placeId}", placeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("place-detail-not-found",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("Place")
                                .summary("장소 상세 조회 실패")
                                .description("존재하지 않는 장소 조회 시 에러를 반환합니다.")
                                .pathParameters(
                                        parameterWithName("placeId").description("장소 고유 ID")
                                )
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("에러 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                        fieldWithPath("timestamp").type(JsonFieldType.STRING).description("에러 발생 시각")
                                )
                                .build()
                        )
                ));
    }

    // =========================================================================
    // GET /api/places
    // =========================================================================
    @Test
    @DisplayName("장소 전체 조회 API - 성공")
    @WithMockCustomUser
    void getAllPlace_success() throws Exception {
        List<PlaceSummaryResponse> places = List.of(
                PlaceSummaryResponse.builder()
                        .placeId(1L)
                        .placeName("도봉산 카페")
                        .category("카페")
                        .thumbnailUrl("https://example.com/thumb1.jpg")
                        .averageRating(4.5)
                        .reviewCount(10L)
                        .isLiked(true)
                        .latitude(37.6898)
                        .longitude(127.0472)
                        .themes(List.of("NATURE"))
                        .build(),
                PlaceSummaryResponse.builder()
                        .placeId(2L)
                        .placeName("도봉 맛집")
                        .category("음식점")
                        .thumbnailUrl("https://example.com/thumb2.jpg")
                        .averageRating(4.0)
                        .reviewCount(5L)
                        .isLiked(false)
                        .latitude(37.6800)
                        .longitude(127.0400)
                        .themes(List.of("RESTAURANT"))
                        .build()
        );

        PlaceSummaryListResponse response = PlaceSummaryListResponse.from(places);

        given(placeService.getAllPlace(eq(1L))).willReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/places")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.responseList").isArray())
                .andExpect(jsonPath("$.data.responseList.length()").value(2))
                .andDo(print())
                .andDo(document("place-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("Place")
                                .summary("전체 장소 조회")
                                .description("전체 장소 목록을 조회합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.responseList[]").type(JsonFieldType.ARRAY).description("장소 목록"),
                                        fieldWithPath("data.responseList[].placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                        fieldWithPath("data.responseList[].placeName").type(JsonFieldType.STRING).description("장소명"),
                                        fieldWithPath("data.responseList[].category").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("data.responseList[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                                        fieldWithPath("data.responseList[].averageRating").type(JsonFieldType.NUMBER).description("평균 평점"),
                                        fieldWithPath("data.responseList[].reviewCount").type(JsonFieldType.NUMBER).description("리뷰 수"),
                                        fieldWithPath("data.responseList[].liked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                        fieldWithPath("data.responseList[].latitude").type(JsonFieldType.NUMBER).description("위도"),
                                        fieldWithPath("data.responseList[].longitude").type(JsonFieldType.NUMBER).description("경도"),
                                        fieldWithPath("data.responseList[].themes").type(JsonFieldType.ARRAY).description("테마 목록")
                                )
                                .build()
                        )
                ));
    }
}
