package com.umust.dobonglife.domain.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.domain.review.controller.dto.request.CreateReviewRequest;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewResponse;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.service.ReviewService;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class ReviewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ReviewService reviewService;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("POST /api/review")
    class RegisterReview {

        @Test
        @DisplayName("리뷰 등록 성공")
        @WithMockCustomUser
        void 리뷰_등록_성공() throws Exception {
            ReviewResponse response = new ReviewResponse(1L, "맛있는 코스였습니다!", 10L);

            when(reviewService.createReview(eq(1L), any(CreateReviewRequest.class), anyList()))
                    .thenReturn(response);

            CreateReviewRequest request = new CreateReviewRequest(1L, null, 4.5, "맛있는 코스였습니다!");
            String requestJson = objectMapper.writeValueAsString(request);

            MockMultipartFile requestPart = new MockMultipartFile(
                    "request", "", MediaType.APPLICATION_JSON_VALUE, requestJson.getBytes()
            );
            MockMultipartFile imagePart = new MockMultipartFile(
                    "imageFiles", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image-data".getBytes()
            );

            mockMvc.perform(multipart("/api/review")
                            .file(requestPart)
                            .file(imagePart)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.reviewId").value(1))
                    .andExpect(jsonPath("$.data.content").value("맛있는 코스였습니다!"))
                    .andExpect(jsonPath("$.data.point").value(10))
                    .andDo(print())
                    .andDo(document("review-register",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestParts(
                                    partWithName("request").description("리뷰 등록 요청 JSON (courseId, placeId, rating, content)"),
                                    partWithName("imageFiles").description("리뷰 이미지 파일 목록 (선택)").optional()
                            ),
                            responseFields(
                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                    fieldWithPath("data.reviewId").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                    fieldWithPath("data.point").type(JsonFieldType.NUMBER).description("적립 포인트")
                            )
                    ));
        }

        @Test
        @DisplayName("이미지 없이 리뷰 등록 성공")
        @WithMockCustomUser
        void 이미지_없이_리뷰_등록_성공() throws Exception {
            ReviewResponse response = new ReviewResponse(2L, "좋은 장소입니다.", 10L);

            when(reviewService.createReview(eq(1L), any(CreateReviewRequest.class), any()))
                    .thenReturn(response);

            CreateReviewRequest request = new CreateReviewRequest(null, 1L, 3.0, "좋은 장소입니다.");
            String requestJson = objectMapper.writeValueAsString(request);

            MockMultipartFile requestPart = new MockMultipartFile(
                    "request", "", MediaType.APPLICATION_JSON_VALUE, requestJson.getBytes()
            );

            mockMvc.perform(multipart("/api/review")
                            .file(requestPart)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.reviewId").value(2))
                    .andExpect(jsonPath("$.data.content").value("좋은 장소입니다."));
        }
    }

    @Nested
    @DisplayName("GET /api/review/course/{courseId}")
    class GetCourseReviews {

        @Test
        @DisplayName("코스 리뷰 조회 성공")
        @WithMockCustomUser
        void 코스_리뷰_조회_성공() throws Exception {
            ReviewSummaryResponse review = new ReviewSummaryResponse(
                    1L, "홍길동", 4.5, "좋은 코스입니다.",
                    List.of("img1.jpg", "img2.jpg"),
                    LocalDateTime.of(2025, 6, 1, 12, 0), false
            );
            CursorResponse<ReviewSummaryResponse> response =
                    new CursorResponse<>(List.of(review), false);

            when(reviewService.getCourseReviews(eq(10L), eq(1L), isNull(), eq(3)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/review/course/{courseId}", 10L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content[0].reviewId").value(1))
                    .andExpect(jsonPath("$.data.content[0].name").value("홍길동"))
                    .andExpect(jsonPath("$.data.content[0].rating").value(4.5))
                    .andExpect(jsonPath("$.data.content[0].content").value("좋은 코스입니다."))
                    .andExpect(jsonPath("$.data.content[0].owner").value(false))
                    .andDo(print())
                    .andDo(document("review-course-list",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("courseId").description("코스 ID")
                            ),
                            queryParameters(
                                    parameterWithName("lastReviewId").optional()
                                            .description("커서 - 마지막 리뷰 ID (첫 요청 시 생략)"),
                                    parameterWithName("size").optional()
                                            .description("조회 개수 (기본값: 3)")
                            ),
                            responseFields(
                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                    fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("리뷰 목록"),
                                    fieldWithPath("data.content[].reviewId").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                    fieldWithPath("data.content[].name").type(JsonFieldType.STRING).description("작성자 이름"),
                                    fieldWithPath("data.content[].rating").type(JsonFieldType.NUMBER).description("평점"),
                                    fieldWithPath("data.content[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                    fieldWithPath("data.content[].imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                    fieldWithPath("data.content[].updatedAt").type(JsonFieldType.STRING).description("수정 일시"),
                                    fieldWithPath("data.content[].owner").type(JsonFieldType.BOOLEAN).description("본인 작성 여부"),
                                    fieldWithPath("data.lastId").type(JsonFieldType.NUMBER).description("마지막 리뷰 ID"),
                                    fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                            )
                    ));
        }

        @Test
        @DisplayName("커서 파라미터 전달 성공")
        @WithMockCustomUser
        void 커서_파라미터_전달_성공() throws Exception {
            CursorResponse<ReviewSummaryResponse> response =
                    new CursorResponse<>(List.of(), false);

            when(reviewService.getCourseReviews(eq(10L), eq(1L), eq(5L), eq(3)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/review/course/{courseId}", 10L)
                            .param("lastReviewId", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty());
        }

        @Test
        @DisplayName("빈 리뷰 목록 성공")
        @WithMockCustomUser
        void 빈_리뷰_목록_성공() throws Exception {
            CursorResponse<ReviewSummaryResponse> response =
                    new CursorResponse<>(List.of(), false);

            when(reviewService.getCourseReviews(eq(99L), eq(1L), isNull(), eq(3)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/review/course/{courseId}", 99L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.hasNext").value(false));
        }
    }

    @Nested
    @DisplayName("GET /api/review/place/{placeId}")
    class GetPlaceReviews {

        @Test
        @DisplayName("장소 리뷰 조회 성공")
        @WithMockCustomUser
        void 장소_리뷰_조회_성공() throws Exception {
            ReviewSummaryResponse review = new ReviewSummaryResponse(
                    2L, "김철수", 3.5, "분위기가 좋아요.",
                    List.of("place-img1.jpg"),
                    LocalDateTime.of(2025, 7, 15, 18, 30), true
            );
            CursorResponse<ReviewSummaryResponse> response =
                    new CursorResponse<>(List.of(review), false);

            when(reviewService.getPlaceReviews(eq(20L), eq(1L), isNull(), eq(3)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/review/place/{placeId}", 20L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content[0].reviewId").value(2))
                    .andExpect(jsonPath("$.data.content[0].name").value("김철수"))
                    .andExpect(jsonPath("$.data.content[0].rating").value(3.5))
                    .andExpect(jsonPath("$.data.content[0].owner").value(true))
                    .andDo(print())
                    .andDo(document("review-place-list",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("placeId").description("장소 ID")
                            ),
                            queryParameters(
                                    parameterWithName("lastReviewId").optional()
                                            .description("커서 - 마지막 리뷰 ID (첫 요청 시 생략)"),
                                    parameterWithName("size").optional()
                                            .description("조회 개수 (기본값: 3)")
                            ),
                            responseFields(
                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                    fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("리뷰 목록"),
                                    fieldWithPath("data.content[].reviewId").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                    fieldWithPath("data.content[].name").type(JsonFieldType.STRING).description("작성자 이름"),
                                    fieldWithPath("data.content[].rating").type(JsonFieldType.NUMBER).description("평점"),
                                    fieldWithPath("data.content[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                    fieldWithPath("data.content[].imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                    fieldWithPath("data.content[].updatedAt").type(JsonFieldType.STRING).description("수정 일시"),
                                    fieldWithPath("data.content[].owner").type(JsonFieldType.BOOLEAN).description("본인 작성 여부"),
                                    fieldWithPath("data.lastId").type(JsonFieldType.NUMBER).description("마지막 리뷰 ID"),
                                    fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                            )
                    ));
        }

        @Test
        @DisplayName("커서 파라미터 전달 성공")
        @WithMockCustomUser
        void 커서_파라미터_전달_성공() throws Exception {
            CursorResponse<ReviewSummaryResponse> response =
                    new CursorResponse<>(List.of(), false);

            when(reviewService.getPlaceReviews(eq(20L), eq(1L), eq(3L), eq(3)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/review/place/{placeId}", 20L)
                            .param("lastReviewId", "3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("DELETE /api/review/{reviewId}")
    class DeleteReview {

        @Test
        @DisplayName("리뷰 삭제 성공")
        @WithMockCustomUser
        void 리뷰_삭제_성공() throws Exception {
            ReviewResponse response = new ReviewResponse(1L, "삭제된 리뷰 내용", 10L);

            when(reviewService.deleteReview(eq(1L), eq(1L)))
                    .thenReturn(response);

            mockMvc.perform(delete("/api/review/{reviewId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.reviewId").value(1))
                    .andExpect(jsonPath("$.data.content").value("삭제된 리뷰 내용"))
                    .andDo(print())
                    .andDo(document("review-delete",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("reviewId").description("삭제할 리뷰 ID")
                            ),
                            responseFields(
                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                    fieldWithPath("data.reviewId").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                    fieldWithPath("data.point").type(JsonFieldType.NUMBER).description("적립 포인트")
                            )
                    ));
        }

        @Test
        @DisplayName("권한 없는 리뷰 삭제 시 에러")
        @WithMockCustomUser
        void 권한_없는_리뷰_삭제_시_에러() throws Exception {
            when(reviewService.deleteReview(eq(1L), eq(1L)))
                    .thenThrow(new BusinessException(ErrorCode.SECURITY_ACCESS_DENIED));

            mockMvc.perform(delete("/api/review/{reviewId}", 1L))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }
}
