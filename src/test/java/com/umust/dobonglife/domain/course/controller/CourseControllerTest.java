package com.umust.dobonglife.domain.course.controller;

import com.umust.dobonglife.domain.course.controller.dto.ReviewSummary;
import com.umust.dobonglife.domain.course.controller.dto.response.*;
import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.service.CourseReviewService;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewSummaryResponse;
import com.umust.dobonglife.global.common.model.BaseStatus;
import com.umust.dobonglife.global.common.response.CursorResponse;
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
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
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
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.SimpleType.INTEGER;
import static com.epages.restdocs.apispec.SimpleType.STRING;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class CourseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CourseService courseService;

    @MockitoBean
    CourseReviewService courseReviewService;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    private CursorResponse<CourseSummaryResponse> createCourseSummaryResponse() {
        CourseSummaryResponse summary = new CourseSummaryResponse(
                1L,
                List.of("img1.jpg"),
                "테스트 코스",
                "테스트 부제목",
                List.of("역사"),
                CourseLevel.BEGINNER,
                false,
                BaseStatus.ACTIVE
        );
        return new CursorResponse<>(List.of(summary), 1L, false);
    }

    // =========================================================================
    // GET /api/course - 코스 목록 조회
    // =========================================================================
    @Nested
    @DisplayName("GET /api/course - 코스 목록 조회")
    class GetCourses {

        @Test
        @WithMockCustomUser
        @DisplayName("코스 목록 조회 성공 200")
        void success() throws Exception {
            int size = 10;
            given(courseService.getCourses(any(), any(), eq(size)))
                    .willReturn(createCourseSummaryResponse());

            mockMvc.perform(get("/api/course")
                            .param("size", String.valueOf(size)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content[0].courseId").value(1))
                    .andExpect(jsonPath("$.data.content[0].title").value("테스트 코스"))
                    .andDo(print())
                    .andDo(document("course-list",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("코스 API")
                                    .summary("코스 목록 조회")
                                    .description("코스 목록을 조회합니다.")
                                    .queryParameters(
                                            parameterWithName("lastId").optional().description("커서 - 마지막 코스 ID (첫 요청 시 생략)").type(INTEGER),
                                            parameterWithName("size").optional().description("조회 개수 (기본값: 2)").type(INTEGER)
                                    )
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("코스 목록"),
                                            fieldWithPath("data.content[].courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                            fieldWithPath("data.content[].imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                            fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("코스 제목"),
                                            fieldWithPath("data.content[].subTitle").type(JsonFieldType.STRING).description("코스 부제목"),
                                            fieldWithPath("data.content[].tags").type(JsonFieldType.ARRAY).description("태그 목록"),
                                            fieldWithPath("data.content[].level").type(JsonFieldType.STRING).description("난이도 (BEGINNER, INTERMEDIATE, ADVANCED)"),
                                            fieldWithPath("data.content[].liked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                            fieldWithPath("data.content[].status").type(JsonFieldType.STRING).description("코스 상태 (ACTIVE, INACTIVE)"),
                                            fieldWithPath("data.lastId").type(JsonFieldType.NUMBER).description("마지막 코스 ID"),
                                            fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                                    )
                                    .build())
                    ));
        }
    }

    // =========================================================================
    // GET /api/course/{courseId} - 코스 상세 조회
    // =========================================================================
    @Nested
    @DisplayName("GET /api/course/{courseId} - 코스 상세 조회")
    class GetCourseDetail {

        @Test
        @WithMockCustomUser
        @DisplayName("코스 상세 조회 성공 200")
        void success() throws Exception {
            CourseDetailResponse.UserInfo userInfo = new CourseDetailResponse.UserInfo(false, true);
            CourseDetailResponse.BasicInfo basicInfo = new CourseDetailResponse.BasicInfo(
                    "테스트 코스", "부제목", 180L, CourseLevel.BEGINNER,
                    List.of(CourseTheme.HISTORY), List.of("역사")
            );
            CourseDetailResponse.DescriptionInfo descriptionInfo = new CourseDetailResponse.DescriptionInfo(
                    "상세 설명입니다", List.of("하이라이트1")
            );
            ReviewSummary reviewSummary = new ReviewSummary(4.0, 5L);

            ReviewSummaryResponse review = new ReviewSummaryResponse(
                    10L, "리뷰어A", 5.0, "좋은 코스입니다",
                    List.of("https://example.com/review1.jpg"),
                    LocalDateTime.of(2025, 6, 1, 10, 0), false
            );
            CursorResponse<ReviewSummaryResponse> reviews = new CursorResponse<>(List.of(review), 10L, false);

            CourseDetailResponse.CoursePlanDto plan = new CourseDetailResponse.CoursePlanDto(
                    1L, 1L, 100L, "도봉서원 방문", "서원 탐방"
            );

            CourseDetailResponse detailResponse = new CourseDetailResponse(
                    1L, userInfo, basicInfo, descriptionInfo, reviewSummary,
                    List.of("img1.jpg"), List.of(plan), reviews
            );

            given(courseReviewService.getCourse(any(), eq(1L), any()))
                    .willReturn(detailResponse);

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/course/{courseId}", 1L)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.basicInfo.title").value("테스트 코스"))
                    .andDo(print())
                    .andDo(document("course-detail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("코스 API")
                                    .summary("코스 상세 조회")
                                    .description("코스 상세 정보와 리뷰를 조회합니다.")
                                    .pathParameters(
                                            parameterWithName("courseId").description("코스 고유 ID").type(INTEGER)
                                    )
                                    .queryParameters(
                                            parameterWithName("lastId").optional().description("리뷰 커서 - 마지막 리뷰 ID (첫 요청 시 생략)").type(INTEGER)
                                    )
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("코스 ID"),
                                            fieldWithPath("data.userInfo.isRemoved").type(JsonFieldType.BOOLEAN).description("삭제된 코스 여부"),
                                            fieldWithPath("data.userInfo.liked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                            fieldWithPath("data.basicInfo.title").type(JsonFieldType.STRING).description("코스 제목"),
                                            fieldWithPath("data.basicInfo.subTitle").type(JsonFieldType.STRING).description("코스 부제목"),
                                            fieldWithPath("data.basicInfo.duration").type(JsonFieldType.NUMBER).description("소요 시간 (분)"),
                                            fieldWithPath("data.basicInfo.level").type(JsonFieldType.STRING).description("난이도"),
                                            fieldWithPath("data.basicInfo.themes").type(JsonFieldType.ARRAY).description("테마 목록"),
                                            fieldWithPath("data.basicInfo.tags").type(JsonFieldType.ARRAY).description("태그 목록"),
                                            fieldWithPath("data.descriptionInfo.content").type(JsonFieldType.STRING).description("상세 설명"),
                                            fieldWithPath("data.descriptionInfo.highlights").type(JsonFieldType.ARRAY).description("하이라이트 목록"),
                                            fieldWithPath("data.reviewSummary.rating").type(JsonFieldType.NUMBER).description("평균 평점"),
                                            fieldWithPath("data.reviewSummary.count").type(JsonFieldType.NUMBER).description("리뷰 수"),
                                            fieldWithPath("data.imageUrls").type(JsonFieldType.ARRAY).description("코스 이미지 URL 목록"),
                                            fieldWithPath("data.plans[]").type(JsonFieldType.ARRAY).description("코스 계획 목록"),
                                            fieldWithPath("data.plans[].id").type(JsonFieldType.NUMBER).description("계획 ID"),
                                            fieldWithPath("data.plans[].order").type(JsonFieldType.NUMBER).description("순서"),
                                            fieldWithPath("data.plans[].placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                            fieldWithPath("data.plans[].title").type(JsonFieldType.STRING).description("계획 제목"),
                                            fieldWithPath("data.plans[].content").type(JsonFieldType.STRING).description("계획 내용"),
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
                                    .build())
                    ));
        }

        @Test
        @WithMockCustomUser
        @DisplayName("존재하지 않는 코스 조회 시 에러")
        void notFound() throws Exception {
            given(courseReviewService.getCourse(any(), eq(999L), any()))
                    .willThrow(new BusinessException(ErrorCode.INVALID_COURSE_ID));

            mockMvc.perform(RestDocumentationRequestBuilders.get("/api/course/{courseId}", 999L)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andDo(print())
                    .andDo(document("course-detail-not-found",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("코스 API")
                                    .summary("코스 상세 조회 실패")
                                    .description("존재하지 않는 코스 조회 시 에러를 반환합니다.")
                                    .pathParameters(
                                            parameterWithName("courseId").description("코스 고유 ID").type(INTEGER)
                                    )
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("에러 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                            fieldWithPath("timestamp").type(JsonFieldType.STRING).description("에러 발생 시각")
                                    )
                                    .build())
                    ));
        }
    }

    // =========================================================================
    // POST /api/course - 코스 등록
    // =========================================================================
    @Nested
    @DisplayName("POST /api/course - 코스 등록")
    class CreateCourse {

        @Test
        @WithMockCustomUser
        @DisplayName("코스 등록 성공 200")
        void success() throws Exception {
            CourseRegisterResponse response = new CourseRegisterResponse(1L, "새 코스", LocalDateTime.now(), 30L);

            given(courseService.createCourse(any(), any(), any()))
                    .willReturn(response);

            String requestJson = """
                    {
                        "title": "도봉구 역사 탐방 코스",
                        "subTitle": "숨겨진 역사를 찾아서",
                        "themes": ["HISTORY"],
                        "duration": 180,
                        "level": "BEGINNER",
                        "tags": ["역사"],
                        "content": "도봉구의 역사적 장소를 둘러보는 코스입니다.",
                        "highlights": ["유적 탐방"],
                        "plans": [{"placeId": 1, "order": 1, "title": "도봉서원 방문", "content": "조선시대 서원 탐방"}]
                    }
                    """;

            MockMultipartFile requestPart = new MockMultipartFile(
                    "request", "", MediaType.APPLICATION_JSON_VALUE, requestJson.getBytes());
            MockMultipartFile imagePart = new MockMultipartFile(
                    "imageFiles", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "image-data".getBytes());

            mockMvc.perform(multipart("/api/course")
                            .file(requestPart)
                            .file(imagePart)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.courseId").value(1))
                    .andDo(print())
                    .andDo(document("course-create",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestParts(
                                    partWithName("request").description("코스 등록 요청 JSON (title, subTitle, themes, duration, level, tags, content, highlights, plans)"),
                                    partWithName("imageFiles").description("코스 이미지 파일 목록").optional()
                            ),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("코스 API")
                                    .summary("코스 등록")
                                    .description("새로운 코스를 등록합니다.")
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data.courseId").type(JsonFieldType.NUMBER).description("생성된 코스 ID"),
                                            fieldWithPath("data.title").type(JsonFieldType.STRING).description("코스 제목"),
                                            fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                                            fieldWithPath("data.point").type(JsonFieldType.NUMBER).description("적립 포인트")
                                    )
                                    .build())
                    ));
        }
    }

    // =========================================================================
    // PATCH /api/course/{courseId} - 코스 수정
    // =========================================================================
    @Nested
    @DisplayName("PATCH /api/course/{courseId} - 코스 수정")
    class UpdateCourse {

        @Test
        @WithMockCustomUser
        @DisplayName("코스 수정 성공 200")
        void success() throws Exception {
            CourseRegisterResponse response = new CourseRegisterResponse(1L, "수정된 코스", LocalDateTime.now(), 0L);

            given(courseService.updateCourse(any(), eq(1L), any(), any()))
                    .willReturn(response);

            String requestJson = """
                    {
                        "title": "수정된 도봉구 역사 탐방 코스",
                        "subTitle": "도봉구의 숨겨진 역사를 찾아서",
                        "themes": ["HISTORY", "CULTURE"],
                        "duration": 180,
                        "level": "INTERMEDIATE",
                        "tags": ["역사", "문화"],
                        "content": "도봉구의 역사적 장소를 둘러보는 수정된 코스입니다.",
                        "highlights": ["조선시대 유적 탐방", "전통 시장 체험"],
                        "plans": [{"placeId": 1, "order": 1, "title": "도봉서원 방문", "content": "조선시대 서원 탐방"}]
                    }
                    """;

            MockMultipartFile requestPart = new MockMultipartFile(
                    "request", "", MediaType.APPLICATION_JSON_VALUE, requestJson.getBytes());
            MockMultipartFile imagePart = new MockMultipartFile(
                    "imageFiles", "updated.jpg", MediaType.IMAGE_JPEG_VALUE, "image-data".getBytes());

            mockMvc.perform(multipart("/api/course/{courseId}", 1L)
                            .file(requestPart)
                            .file(imagePart)
                            .with(request -> { request.setMethod("PATCH"); return request; })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.courseId").value(1))
                    .andExpect(jsonPath("$.data.title").value("수정된 코스"))
                    .andDo(print())
                    .andDo(document("course-update",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestParts(
                                    partWithName("request").description("코스 수정 요청 JSON (title, subTitle, urlsToDelete, themes, duration, level, tags, content, highlights, plans)"),
                                    partWithName("imageFiles").description("코스 이미지 파일 목록").optional()
                            ),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("코스 API")
                                    .summary("코스 수정")
                                    .description("코스를 수정합니다.")
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data.courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                            fieldWithPath("data.title").type(JsonFieldType.STRING).description("코스 제목"),
                                            fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                                            fieldWithPath("data.point").type(JsonFieldType.NUMBER).description("적립 포인트")
                                    )
                                    .build())
                    ));
        }
    }

    // =========================================================================
    // DELETE /api/course/{courseId} - 코스 삭제
    // =========================================================================
    @Nested
    @DisplayName("DELETE /api/course/{courseId} - 코스 삭제")
    class DeleteCourse {

        @Test
        @WithMockCustomUser
        @DisplayName("코스 삭제 성공 200")
        void success() throws Exception {
            given(courseService.deleteCourse(any(), eq(1L)))
                    .willReturn(new CourseDeleteResponse(1L));

            mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/course/{courseId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.courseId").value(1))
                    .andDo(print())
                    .andDo(document("course-delete",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("코스 API")
                                    .summary("코스 삭제")
                                    .description("코스를 삭제합니다.")
                                    .pathParameters(
                                            parameterWithName("courseId").description("코스 고유 ID").type(INTEGER)
                                    )
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data.courseId").type(JsonFieldType.NUMBER).description("삭제된 코스 ID")
                                    )
                                    .build())
                    ));
        }
    }

    // =========================================================================
    // GET /api/course/my - 내 코스 조회
    // =========================================================================
    @Nested
    @DisplayName("GET /api/course/my - 내 코스 조회")
    class GetMyCourses {

        @Test
        @WithMockCustomUser
        @DisplayName("내 코스 조회 성공 200")
        void success() throws Exception {
            int size = 10;
            CursorResponse<CourseSummaryResponse> cursorResponse = createCourseSummaryResponse();
            CourseMyResponse myResponse = new CourseMyResponse(1L, cursorResponse);

            given(courseService.getMyCourses(any(), eq(size), any()))
                    .willReturn(myResponse);

            mockMvc.perform(get("/api/course/my")
                            .param("size", String.valueOf(size)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.totalCount").value(1))
                    .andDo(print())
                    .andDo(document("course-my",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("코스 API")
                                    .summary("내 코스 조회")
                                    .description("내가 등록한 코스 목록을 조회합니다.")
                                    .queryParameters(
                                            parameterWithName("lastId").optional().description("커서 - 마지막 코스 ID (첫 요청 시 생략)").type(INTEGER),
                                            parameterWithName("size").optional().description("조회 개수 (기본값: 2)").type(INTEGER)
                                    )
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data.totalCount").type(JsonFieldType.NUMBER).description("총 코스 수"),
                                            fieldWithPath("data.course.content[]").type(JsonFieldType.ARRAY).description("코스 목록"),
                                            fieldWithPath("data.course.content[].courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                            fieldWithPath("data.course.content[].imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL"),
                                            fieldWithPath("data.course.content[].title").type(JsonFieldType.STRING).description("코스 제목"),
                                            fieldWithPath("data.course.content[].subTitle").type(JsonFieldType.STRING).description("코스 부제목"),
                                            fieldWithPath("data.course.content[].tags").type(JsonFieldType.ARRAY).description("태그 목록"),
                                            fieldWithPath("data.course.content[].level").type(JsonFieldType.STRING).description("난이도"),
                                            fieldWithPath("data.course.content[].liked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                            fieldWithPath("data.course.content[].status").type(JsonFieldType.STRING).description("코스 상태 (ACTIVE, INACTIVE)"),
                                            fieldWithPath("data.course.lastId").type(JsonFieldType.NUMBER).description("마지막 코스 ID"),
                                            fieldWithPath("data.course.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                                    )
                                    .build())
                    ));
        }
    }

    // =========================================================================
    // GET /api/course/theme - 테마별 코스 조회
    // =========================================================================
    @Nested
    @DisplayName("GET /api/course/theme - 테마별 코스 조회")
    class GetCoursesByTheme {

        @Test
        @WithMockCustomUser
        @DisplayName("테마별 코스 조회 성공 200")
        void success() throws Exception {
            int size = 10;
            given(courseService.getCourses(any(), eq(CourseTheme.HISTORY), any(), eq(size)))
                    .willReturn(createCourseSummaryResponse());

            mockMvc.perform(get("/api/course/theme")
                            .param("theme", "HISTORY")
                            .param("size", String.valueOf(size)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content[0].courseId").value(1))
                    .andDo(print())
                    .andDo(document("course-theme",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("코스 API")
                                    .summary("주간 테마별 코스 조회")
                                    .description("주간테마별 코스를 조회합니다. theme은 영어로 보내주시면 됩니다. (예시: HISTORY)")
                                    .queryParameters(
                                            parameterWithName("theme").description("테마 (HISTORY, NATURE, CULTURE 등)").type(STRING),
                                            parameterWithName("lastId").optional().description("커서 - 마지막 코스 ID (첫 요청 시 생략)").type(INTEGER),
                                            parameterWithName("size").optional().description("조회 개수 (기본값: 2)").type(INTEGER)
                                    )
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("코스 목록"),
                                            fieldWithPath("data.content[].courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                            fieldWithPath("data.content[].imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL"),
                                            fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("코스 제목"),
                                            fieldWithPath("data.content[].subTitle").type(JsonFieldType.STRING).description("코스 부제목"),
                                            fieldWithPath("data.content[].tags").type(JsonFieldType.ARRAY).description("태그 목록"),
                                            fieldWithPath("data.content[].level").type(JsonFieldType.STRING).description("난이도"),
                                            fieldWithPath("data.content[].liked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                            fieldWithPath("data.content[].status").type(JsonFieldType.STRING).description("코스 상태 (ACTIVE, INACTIVE)"),
                                            fieldWithPath("data.lastId").type(JsonFieldType.NUMBER).description("마지막 코스 ID"),
                                            fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                                    )
                                    .build())
                    ));
        }
    }
}
