package com.umust.dobonglife.content.controller;

import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.course.application.CourseDetailService;
import com.umust.dobonglife.domain.course.application.CourseService;
import com.umust.dobonglife.domain.course.application.dto.*;
import com.umust.dobonglife.domain.course.application.dto.CourseDetailResponse.CoursePlanDetail;
import com.umust.dobonglife.domain.course.domain.vo.Level;
import com.umust.dobonglife.domain.place.domain.vo.Theme;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.global.common.model.BaseStatus;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.test.support.TestWebMvcConfig;
import com.umust.dobonglife.test.support.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.SimpleType.INTEGER;
import static com.epages.restdocs.apispec.SimpleType.STRING;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CourseController.class, excludeAutoConfiguration = {
        OAuth2ClientAutoConfiguration.class,
        OAuth2ClientWebSecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class})
@Import(TestWebMvcConfig.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class CourseControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CourseService courseService;
    @MockitoBean
    CourseDetailService courseDetailService;
    @MockitoBean
    AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;
    @MockitoBean
    TokenExtractor tokenExtractor;

    @Test
    @DisplayName("코스 생성 - 성공")
    @WithMockCustomUser
    void createCourse_success() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest(
                "도봉산 힐링 코스", "자연과 함께하는 힐링", Level.BEGINNER, 120L,
                "도봉산 주변 힐링 코스입니다.", List.of("https://img.test/1.jpg"),
                List.of(Theme.NATURE),
                List.of(new CreateCourseRequest.CoursePlanRequest(1L, "도봉산 입구", "출발지점")),
                List.of("힐링", "자연")
        );

        given(courseService.createCourse(eq(1L), any(CreateCourseRequest.class)))
                .willReturn(new CourseRegisterResponse(1L));

        mockMvc.perform(post("/api/course")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.courseId").value(1L))
                .andDo(print())
                .andDo(document("course-create",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("코스 API").summary("코스 생성")
                                .description("새로운 코스를 생성합니다.")
                                .requestFields(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("코스 제목"),
                                        fieldWithPath("subTitle").type(JsonFieldType.STRING).description("부제목"),
                                        fieldWithPath("level").type(JsonFieldType.STRING).description("난이도 (BEGINNER, INTERMEDIATE, ADVANCED)"),
                                        fieldWithPath("duration").type(JsonFieldType.NUMBER).description("소요 시간(분)"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("코스 설명"),
                                        fieldWithPath("imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                        fieldWithPath("themes").type(JsonFieldType.ARRAY).description("테마 목록"),
                                        fieldWithPath("plans").type(JsonFieldType.ARRAY).description("코스 계획 목록"),
                                        fieldWithPath("plans[].placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                        fieldWithPath("plans[].title").type(JsonFieldType.STRING).description("계획 제목"),
                                        fieldWithPath("plans[].content").type(JsonFieldType.STRING).description("계획 설명"),
                                        fieldWithPath("tags").type(JsonFieldType.ARRAY).description("태그 목록"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.courseId").type(JsonFieldType.NUMBER).description("생성된 코스 ID"))
                                .build())
                ));

        verify(courseService).createCourse(eq(1L), any(CreateCourseRequest.class));
    }

    @Test
    @DisplayName("코스 수정 - 성공")
    @WithMockCustomUser
    void updateCourse_success() throws Exception {
        UpdateCourseRequest request = new UpdateCourseRequest(
                "수정된 코스", "수정된 부제", Level.INTERMEDIATE, 90L,
                "수정된 설명", List.of("https://img.test/new.jpg"), List.of("https://img.test/old.jpg"),
                List.of(Theme.NATURE),
                List.of(new CreateCourseRequest.CoursePlanRequest(1L, "수정된 계획", "수정된 내용")),
                List.of("수정태그")
        );

        given(courseService.updateCourse(eq(1L), eq(1L), any(UpdateCourseRequest.class)))
                .willReturn(new CourseRegisterResponse(1L));

        mockMvc.perform(patch("/api/course/{courseId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.courseId").value(1L))
                .andDo(print())
                .andDo(document("course-update",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("코스 API").summary("코스 수정")
                                .description("기존 코스를 수정합니다.")
                                .pathParameters(parameterWithName("courseId").description("코스 ID"))
                                .requestFields(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("코스 제목"),
                                        fieldWithPath("subTitle").type(JsonFieldType.STRING).description("부제목"),
                                        fieldWithPath("level").type(JsonFieldType.STRING).description("난이도"),
                                        fieldWithPath("duration").type(JsonFieldType.NUMBER).description("소요 시간(분)"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("코스 설명"),
                                        fieldWithPath("newImageUrls").type(JsonFieldType.ARRAY).description("새로 추가할 이미지 URL"),
                                        fieldWithPath("deleteImageUrls").type(JsonFieldType.ARRAY).description("삭제할 이미지 URL"),
                                        fieldWithPath("themes").type(JsonFieldType.ARRAY).description("테마 목록"),
                                        fieldWithPath("plans").type(JsonFieldType.ARRAY).description("코스 계획 목록"),
                                        fieldWithPath("plans[].placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                        fieldWithPath("plans[].title").type(JsonFieldType.STRING).description("계획 제목"),
                                        fieldWithPath("plans[].content").type(JsonFieldType.STRING).description("계획 설명"),
                                        fieldWithPath("tags").type(JsonFieldType.ARRAY).description("태그 목록"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.courseId").type(JsonFieldType.NUMBER).description("코스 ID"))
                                .build())
                ));

        verify(courseService).updateCourse(eq(1L), eq(1L), any(UpdateCourseRequest.class));
    }

    @Test
    @DisplayName("내 코스 조회 - 성공")
    @WithMockCustomUser
    void getMyCourses_success() throws Exception {
        CourseSummaryResponse summary = new CourseSummaryResponse(
                1L, "도봉산 코스", "힐링 코스", "BEGINNER", 120L,
                "https://img.test/thumb.jpg", 4.5, 10L, true);
        CursorResponse<CourseSummaryResponse> cursor = new CursorResponse<>(List.of(summary), false);
        MyCourseResponse myCourseResponse = MyCourseResponse.of(1L, cursor);

        given(courseService.getMyCourses(eq(1L), any(), anyInt()))
                .willReturn(myCourseResponse);

        mockMvc.perform(get("/api/course/my").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCount").value(1))
                .andDo(print())
                .andDo(document("course-get-my",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("코스 API").summary("내 코스 조회")
                                .description("로그인한 사용자의 코스 목록을 조회합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.totalCount").type(JsonFieldType.NUMBER).description("총 코스 수"),
                                        fieldWithPath("data.courses.content[].courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                        fieldWithPath("data.courses.content[].title").type(JsonFieldType.STRING).description("코스 제목"),
                                        fieldWithPath("data.courses.content[].subTitle").type(JsonFieldType.STRING).description("부제목"),
                                        fieldWithPath("data.courses.content[].level").type(JsonFieldType.STRING).description("난이도"),
                                        fieldWithPath("data.courses.content[].duration").type(JsonFieldType.NUMBER).description("소요 시간"),
                                        fieldWithPath("data.courses.content[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                                        fieldWithPath("data.courses.content[].averageRating").type(JsonFieldType.NUMBER).description("평균 평점"),
                                        fieldWithPath("data.courses.content[].reviewCount").type(JsonFieldType.NUMBER).description("리뷰 수"),
                                        fieldWithPath("data.courses.content[].isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                        fieldWithPath("data.courses.lastId").type(JsonFieldType.NUMBER).description("마지막 코스 ID"),
                                        fieldWithPath("data.courses.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"))
                                .build())
                ));

        verify(courseService).getMyCourses(eq(1L), any(), anyInt());
    }

    @Test
    @DisplayName("전체 코스 조회 - 성공")
    @WithMockCustomUser
    void getAllCourses_success() throws Exception {
        CourseSummaryResponse summary = new CourseSummaryResponse(
                1L, "도봉산 코스", "힐링 코스", "BEGINNER", 120L,
                "https://img.test/thumb.jpg", 4.5, 10L, false);
        CursorResponse<CourseSummaryResponse> cursor = new CursorResponse<>(List.of(summary), false);

        given(courseService.getAllCourses(eq(1L), any(), any(), anyInt()))
                .willReturn(cursor);

        mockMvc.perform(get("/api/course").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("도봉산 코스"))
                .andDo(print())
                .andDo(document("course-get-all",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("코스 API").summary("전체 코스 조회")
                                .description("전체 코스 목록을 커서 기반으로 조회합니다.")
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
                                        fieldWithPath("data.lastId").type(JsonFieldType.NUMBER).description("마지막 코스 ID"),
                                        fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"))
                                .build())
                ));

        verify(courseService).getAllCourses(eq(1L), any(), any(), anyInt());
    }

    @Test
    @DisplayName("코스 삭제 - 성공")
    @WithMockCustomUser
    void deleteCourse_success() throws Exception {
        mockMvc.perform(delete("/api/course/{courseId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("course-delete",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("코스 API").summary("코스 삭제")
                                .description("코스를 삭제합니다.")
                                .pathParameters(parameterWithName("courseId").description("코스 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 없음"))
                                .build())
                ));

        verify(courseService).deleteCourse(eq(1L), eq(1L));
    }

    @Test
    @DisplayName("코스 상세 조회 - 성공")
    @WithMockCustomUser
    void getCourseDetail_success() throws Exception {
        CursorResponse<ReviewSummaryResponse> emptyReviews = new CursorResponse<>(List.of(), false);
        CourseDetailResponse response = new CourseDetailResponse(
                1L, "도봉산 코스", "힐링 코스", "BEGINNER", 120L,
                "도봉산 주변 힐링 코스입니다.",
                List.of("https://img.test/1.jpg"),
                List.of("NATURE"),
                List.of("힐링"),
                List.of(new CoursePlanDetail(1L, 1L, (short) 0, "도봉산 입구", "출발지점")),
                4.5, 10L, true, emptyReviews);

        given(courseDetailService.getCourseDetail(eq(1L), eq(1L), any(), anyInt()))
                .willReturn(response);

        mockMvc.perform(get("/api/course/{courseId}", 1L).param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("도봉산 코스"))
                .andExpect(jsonPath("$.data.isLiked").value(true))
                .andDo(print())
                .andDo(document("course-get-detail",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("코스 API").summary("코스 상세 조회")
                                .description("코스 상세 정보와 리뷰를 조회합니다.")
                                .pathParameters(parameterWithName("courseId").description("코스 ID"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("코스 제목"),
                                        fieldWithPath("data.subTitle").type(JsonFieldType.STRING).description("부제목"),
                                        fieldWithPath("data.level").type(JsonFieldType.STRING).description("난이도"),
                                        fieldWithPath("data.duration").type(JsonFieldType.NUMBER).description("소요 시간"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("코스 설명"),
                                        fieldWithPath("data.imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                        fieldWithPath("data.themes").type(JsonFieldType.ARRAY).description("테마 목록"),
                                        fieldWithPath("data.tags").type(JsonFieldType.ARRAY).description("태그 목록"),
                                        fieldWithPath("data.plans[].id").type(JsonFieldType.NUMBER).description("계획 ID"),
                                        fieldWithPath("data.plans[].placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                        fieldWithPath("data.plans[].sortOrder").type(JsonFieldType.NUMBER).description("정렬 순서"),
                                        fieldWithPath("data.plans[].title").type(JsonFieldType.STRING).description("계획 제목"),
                                        fieldWithPath("data.plans[].content").type(JsonFieldType.STRING).description("계획 설명"),
                                        fieldWithPath("data.averageRating").type(JsonFieldType.NUMBER).description("평균 평점"),
                                        fieldWithPath("data.reviewCount").type(JsonFieldType.NUMBER).description("리뷰 수"),
                                        fieldWithPath("data.isLiked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                        fieldWithPath("data.reviews.content").type(JsonFieldType.ARRAY).description("리뷰 목록"),
                                        fieldWithPath("data.reviews.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 존재 여부"),
                                        fieldWithPath("data.reviews.lastId").type(JsonFieldType.NULL).description("마지막 리뷰 ID"))
                                .build())
                ));

        verify(courseDetailService).getCourseDetail(eq(1L), eq(1L), any(), anyInt());
    }
}
