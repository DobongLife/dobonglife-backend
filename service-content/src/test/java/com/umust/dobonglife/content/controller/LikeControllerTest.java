package com.umust.dobonglife.content.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.like.application.LikeService;
import com.umust.dobonglife.domain.like.application.dto.MyLikedCourseResponse;
import com.umust.dobonglife.domain.like.application.dto.MyLikedPlaceResponse;
import com.umust.dobonglife.global.common.constant.TargetType;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = LikeController.class, excludeAutoConfiguration = {
        OAuth2ClientAutoConfiguration.class,
        OAuth2ClientWebSecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class})
@Import(TestWebMvcConfig.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class LikeControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean LikeService likeService;
    @MockitoBean AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;
    @MockitoBean TokenExtractor tokenExtractor;

    @Test
    @DisplayName("장소 좋아요 토글 - 성공")
    @WithMockCustomUser
    void likePlace_success() throws Exception {
        given(likeService.toggleLike(eq(1L), eq(TargetType.PLACE), eq(1L)))
                .willReturn(true);

        mockMvc.perform(post("/api/like/place/{placeId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true))
                .andDo(print())
                .andDo(document("like-place-toggle",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("좋아요 API").summary("장소 좋아요 토글")
                                .description("장소 좋아요를 토글합니다.")
                                .pathParameters(parameterWithName("placeId").description("장소 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.BOOLEAN).description("좋아요 상태 (true: 좋아요, false: 취소)"))
                                .build())
                ));

        verify(likeService).toggleLike(eq(1L), eq(TargetType.PLACE), eq(1L));
    }

    @Test
    @DisplayName("내 좋아요 장소 목록 조회 - 성공")
    @WithMockCustomUser
    void getMyLikedPlaces_success() throws Exception {
        MyLikedPlaceResponse place = new MyLikedPlaceResponse(
                1L, "도봉산 카페", "CAFE", "https://img.test/1.jpg",
                4.5, 10L, true, 37.6584, 127.0295, List.of("NATURE"), BaseStatus.ACTIVE);
        CursorResponse<MyLikedPlaceResponse> cursor = new CursorResponse<>(List.of(place), false);

        given(likeService.getMyLikedPlaces(eq(1L), any(), anyInt()))
                .willReturn(cursor);

        mockMvc.perform(get("/api/like/place/my").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].placeName").value("도봉산 카페"))
                .andDo(print())
                .andDo(document("like-place-my",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("좋아요 API").summary("내 좋아요 장소 조회")
                                .description("내가 좋아요한 장소 목록을 조회합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.content[].placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                        fieldWithPath("data.content[].placeName").type(JsonFieldType.STRING).description("장소명"),
                                        fieldWithPath("data.content[].category").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("data.content[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                                        fieldWithPath("data.content[].averageRating").type(JsonFieldType.NUMBER).description("평균 평점"),
                                        fieldWithPath("data.content[].reviewCount").type(JsonFieldType.NUMBER).description("리뷰 수"),
                                        fieldWithPath("data.content[].isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                        fieldWithPath("data.content[].latitude").type(JsonFieldType.NUMBER).description("위도"),
                                        fieldWithPath("data.content[].longitude").type(JsonFieldType.NUMBER).description("경도"),
                                        fieldWithPath("data.content[].themes").type(JsonFieldType.ARRAY).description("테마 목록"),
                                        fieldWithPath("data.content[].status").type(JsonFieldType.STRING).description("상태"),
                                        fieldWithPath("data.lastId").type(JsonFieldType.NUMBER).description("마지막 장소 ID"),
                                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"))
                                .build())
                ));

        verify(likeService).getMyLikedPlaces(eq(1L), any(), anyInt());
    }

    @Test
    @DisplayName("코스 좋아요 토글 - 성공")
    @WithMockCustomUser
    void likeCourse_success() throws Exception {
        given(likeService.toggleLike(eq(1L), eq(TargetType.COURSE), eq(1L)))
                .willReturn(false);

        mockMvc.perform(post("/api/like/course/{courseId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false))
                .andDo(print())
                .andDo(document("like-course-toggle",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("좋아요 API").summary("코스 좋아요 토글")
                                .description("코스 좋아요를 토글합니다.")
                                .pathParameters(parameterWithName("courseId").description("코스 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.BOOLEAN).description("좋아요 상태 (true: 좋아요, false: 취소)"))
                                .build())
                ));

        verify(likeService).toggleLike(eq(1L), eq(TargetType.COURSE), eq(1L));
    }

    @Test
    @DisplayName("내 좋아요 코스 목록 조회 - 성공")
    @WithMockCustomUser
    void getMyLikedCourses_success() throws Exception {
        MyLikedCourseResponse course = new MyLikedCourseResponse(
                1L, "도봉산 코스", "힐링 코스", "BEGINNER", 120L,
                "https://img.test/thumb.jpg", 4.5, 10L, true,
                List.of("NATURE"), BaseStatus.ACTIVE);
        CursorResponse<MyLikedCourseResponse> cursor = new CursorResponse<>(List.of(course), false);

        given(likeService.getMyLikedCourses(eq(1L), any(), anyInt()))
                .willReturn(cursor);

        mockMvc.perform(get("/api/like/course/my").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("도봉산 코스"))
                .andDo(print())
                .andDo(document("like-course-my",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("좋아요 API").summary("내 좋아요 코스 조회")
                                .description("내가 좋아요한 코스 목록을 조회합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.content[].courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                        fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("코스 제목"),
                                        fieldWithPath("data.content[].subTitle").type(JsonFieldType.STRING).description("부제목"),
                                        fieldWithPath("data.content[].level").type(JsonFieldType.STRING).description("난이도"),
                                        fieldWithPath("data.content[].duration").type(JsonFieldType.NUMBER).description("소요 시간"),
                                        fieldWithPath("data.content[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                                        fieldWithPath("data.content[].averageRating").type(JsonFieldType.NUMBER).description("평균 평점"),
                                        fieldWithPath("data.content[].reviewCount").type(JsonFieldType.NUMBER).description("리뷰 수"),
                                        fieldWithPath("data.content[].isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                        fieldWithPath("data.content[].themes").type(JsonFieldType.ARRAY).description("테마 목록"),
                                        fieldWithPath("data.content[].status").type(JsonFieldType.STRING).description("상태"),
                                        fieldWithPath("data.lastId").type(JsonFieldType.NUMBER).description("마지막 코스 ID"),
                                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"))
                                .build())
                ));

        verify(likeService).getMyLikedCourses(eq(1L), any(), anyInt());
    }
}
