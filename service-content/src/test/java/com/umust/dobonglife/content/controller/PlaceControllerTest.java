package com.umust.dobonglife.content.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.content.facade.PlaceListFacade;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.place.application.PlaceReviewService;
import com.umust.dobonglife.domain.place.application.dto.PlaceDetailResponse;
import com.umust.dobonglife.domain.place.application.dto.PlaceSummaryResponse;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.global.common.model.BaseStatus;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PlaceController.class, excludeAutoConfiguration = {
        OAuth2ClientAutoConfiguration.class,
        OAuth2ClientWebSecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class})
@Import(TestWebMvcConfig.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class PlaceControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean PlaceReviewService placeReviewService;
    @MockitoBean PlaceListFacade placeListFacade;
    @MockitoBean AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;
    @MockitoBean TokenExtractor tokenExtractor;

    @Test
    @DisplayName("전체 장소 조회 - 성공")
    @WithMockCustomUser
    void getAllPlaces_success() throws Exception {
        given(placeListFacade.getAllPlaces(eq(1L)))
                .willReturn(List.of(
                        new PlaceSummaryResponse(1L, "도봉산 카페", "CAFE", "https://img.test/1.jpg",
                                4.5, 10L, false, 37.6584, 127.0295, List.of("NATURE"), BaseStatus.ACTIVE)
                ));

        mockMvc.perform(get("/api/place"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].placeName").value("도봉산 카페"))
                .andExpect(jsonPath("$.data[0].isLiked").value(false))
                .andDo(print())
                .andDo(document("place-get-all",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("장소 API").summary("전체 장소 조회")
                                .description("모든 활성 장소를 조회합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data[].placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                        fieldWithPath("data[].placeName").type(JsonFieldType.STRING).description("장소명"),
                                        fieldWithPath("data[].category").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("data[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                                        fieldWithPath("data[].averageRating").type(JsonFieldType.NUMBER).description("평균 평점"),
                                        fieldWithPath("data[].reviewCount").type(JsonFieldType.NUMBER).description("리뷰 수"),
                                        fieldWithPath("data[].isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                        fieldWithPath("data[].latitude").type(JsonFieldType.NUMBER).description("위도"),
                                        fieldWithPath("data[].longitude").type(JsonFieldType.NUMBER).description("경도"),
                                        fieldWithPath("data[].themes").type(JsonFieldType.ARRAY).description("테마 목록"),
                                        fieldWithPath("data[].status").type(JsonFieldType.STRING).description("상태"))
                                .build())
                ));

        verify(placeListFacade).getAllPlaces(eq(1L));
    }

    @Test
    @DisplayName("장소 상세 조회 - 성공")
    @WithMockCustomUser
    void getPlaceDetail_success() throws Exception {
        CursorResponse<ReviewSummaryResponse> emptyReviews = new CursorResponse<>(List.of(), false);
        PlaceDetailResponse response = new PlaceDetailResponse(
                1L, "도봉산 카페", "도봉구 대표 카페", "CAFE", "도봉산 입구에 위치",
                "서울시 도봉구 도봉로 100", List.of("https://img.test/1.jpg"),
                "09:00 - 22:00", "02-1234-5678", List.of("NATURE"),
                4.5, 10L, 37.6584, 127.0295, true, emptyReviews);

        given(placeReviewService.getPlaceDetail(eq(1L), eq(1L), any(), anyInt()))
                .willReturn(response);

        mockMvc.perform(get("/api/place/{placeId}", 1L).param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("도봉산 카페"))
                .andExpect(jsonPath("$.data.isLiked").value(true))
                .andDo(print())
                .andDo(document("place-get-detail",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("장소 API").summary("장소 상세 조회")
                                .description("장소 상세 정보와 리뷰를 조회합니다.")
                                .pathParameters(parameterWithName("placeId").description("장소 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("장소명"),
                                        fieldWithPath("data.subName").type(JsonFieldType.STRING).description("부제"),
                                        fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("설명"),
                                        fieldWithPath("data.address").type(JsonFieldType.STRING).description("주소"),
                                        fieldWithPath("data.placeImages").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                        fieldWithPath("data.operatingHour").type(JsonFieldType.STRING).description("영업시간"),
                                        fieldWithPath("data.contact").type(JsonFieldType.STRING).description("연락처"),
                                        fieldWithPath("data.themes").type(JsonFieldType.ARRAY).description("테마 목록"),
                                        fieldWithPath("data.averageRating").type(JsonFieldType.NUMBER).description("평균 평점"),
                                        fieldWithPath("data.reviewCount").type(JsonFieldType.NUMBER).description("리뷰 수"),
                                        fieldWithPath("data.latitude").type(JsonFieldType.NUMBER).description("위도"),
                                        fieldWithPath("data.longitude").type(JsonFieldType.NUMBER).description("경도"),
                                        fieldWithPath("data.isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                        fieldWithPath("data.reviews.content").type(JsonFieldType.ARRAY).description("리뷰 목록"),
                                        fieldWithPath("data.reviews.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                                        fieldWithPath("data.reviews.lastId").type(JsonFieldType.NULL).description("마지막 리뷰 ID"))
                                .build())
                ));

        verify(placeReviewService).getPlaceDetail(eq(1L), eq(1L), any(), anyInt());
    }
}
