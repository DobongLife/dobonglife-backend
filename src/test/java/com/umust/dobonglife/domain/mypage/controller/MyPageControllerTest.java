package com.umust.dobonglife.domain.mypage.controller;

import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.mypage.controller.dto.response.MyCourseLikeResponse;
import com.umust.dobonglife.domain.mypage.controller.dto.response.MyPageSummaryResponse;
import com.umust.dobonglife.domain.mypage.controller.dto.response.MyPlaceLikeResponse;
import com.umust.dobonglife.domain.mypage.service.MyPageService;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceSummaryResponse;
import com.umust.dobonglife.domain.point.controller.dto.response.PointResponse;
import com.umust.dobonglife.domain.user.controller.dto.response.MyPageResponse;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.slice.SliceResponse;
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

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class MyPageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    MyPageService myPageService;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Nested
    @DisplayName("GET /api/my")
    class ViewMyPage {

        @Test
        @DisplayName("마이페이지 조회 성공")
        @WithMockCustomUser
        void 마이페이지_조회_성공() throws Exception {
            MyPageResponse userInfo = new MyPageResponse("홍길동", "test@example.com", Role.MEMBER);

            PointResponse pr = PointResponse.builder()
                    .pointId(1L)
                    .title("후기 작성")
                    .amount(10L)
                    .createAt(LocalDateTime.of(2025, 1, 1, 12, 0))
                    .afterBalance(510L)
                    .isUsed(false)
                    .build();
            SliceResponse<PointResponse> pointList = SliceResponse.<PointResponse>builder()
                    .content(List.of(pr))
                    .size(3)
                    .hasNext(false)
                    .nextCursor(null)
                    .build();

            MyPageSummaryResponse response = new MyPageSummaryResponse(userInfo, pointList);

            when(myPageService.getMyPageSummary(eq(1L), eq(3)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/my"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userInfo.name").value("홍길동"))
                    .andExpect(jsonPath("$.data.userInfo.email").value("test@example.com"))
                    .andExpect(jsonPath("$.data.userInfo.role").value("MEMBER"))
                    .andExpect(jsonPath("$.data.pointList.content[0].pointId").value(1))
                    .andExpect(jsonPath("$.data.pointList.content[0].title").value("후기 작성"))
                    .andExpect(jsonPath("$.data.pointList.content[0].amount").value(10))
                    .andDo(print())
                    .andDo(document("mypage-main",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("마이 페이지 API")
                                            .summary("마이페이지 조회")
                                            .description("마이페이지 정보와 포인트 내역을 조회합니다.")
                                            .queryParameters(
                                                    parameterWithName("size").optional()
                                                            .description("포인트 내역 조회 개수 (기본값: 3)")
                                            )
                                            .responseFields(
                                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),

                                                    fieldWithPath("data.userInfo.name").type(JsonFieldType.STRING).description("사용자 이름"),
                                                    fieldWithPath("data.userInfo.email").type(JsonFieldType.STRING).description("사용자 이메일"),
                                                    fieldWithPath("data.userInfo.role").type(JsonFieldType.STRING).description("사용자 역할 (MEMBER/MANAGER/ADMIN)"),

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
        @DisplayName("빈 포인트 내역 마이페이지 조회 성공")
        @WithMockCustomUser
        void 빈_포인트_내역_마이페이지_조회_성공() throws Exception {
            MyPageResponse userInfo = new MyPageResponse("홍길동", "test@example.com", Role.MEMBER);
            SliceResponse<PointResponse> pointList = SliceResponse.<PointResponse>builder()
                    .content(List.of())
                    .size(3)
                    .hasNext(false)
                    .nextCursor(null)
                    .build();
            MyPageSummaryResponse response = new MyPageSummaryResponse(userInfo, pointList);

            when(myPageService.getMyPageSummary(eq(1L), eq(3)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/my"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.userInfo.name").value("홍길동"))
                    .andExpect(jsonPath("$.data.pointList.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/my/like/course")
    class ViewMyCourseLike {

        @Test
        @DisplayName("찜한 코스 조회 성공")
        @WithMockCustomUser
        void 찜한_코스_조회_성공() throws Exception {
            CourseSummaryResponse course = new CourseSummaryResponse(
                    1L, List.of("img1.jpg"), "도봉산 코스",
                    "아름다운 도봉산 산책로", List.of("자연", "힐링"),
                    CourseLevel.BEGINNER, true
            );
            CursorResponse<CourseSummaryResponse> courseList =
                    new CursorResponse<>(List.of(course), false);

            MyCourseLikeResponse response = new MyCourseLikeResponse(5L, courseList);

            when(myPageService.getMyCourseLike(eq(1L), eq(3), isNull()))
                    .thenReturn(response);

            mockMvc.perform(get("/api/my/like/course"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.totalCount").value(5))
                    .andExpect(jsonPath("$.data.course.content[0].courseId").value(1))
                    .andExpect(jsonPath("$.data.course.content[0].title").value("도봉산 코스"))
                    .andExpect(jsonPath("$.data.course.content[0].level").value("BEGINNER"))
                    .andExpect(jsonPath("$.data.course.content[0].liked").value(true))
                    .andDo(print())
                    .andDo(document("mypage-like-course",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("마이 페이지 API")
                                            .summary("찜한 코스 조회")
                                            .description("찜한 코스 목록을 조회합니다.")
                                            .queryParameters(
                                                    parameterWithName("lastId").optional()
                                                            .description("커서 - 마지막 코스 ID (첫 요청 시 생략)"),
                                                    parameterWithName("size").optional()
                                                            .description("조회 개수 (기본값: 3)")
                                            )
                                            .responseFields(
                                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),

                                                    fieldWithPath("data.totalCount").type(JsonFieldType.NUMBER).description("찜한 코스 총 개수"),
                                                    fieldWithPath("data.course.content[]").type(JsonFieldType.ARRAY).description("코스 목록"),
                                                    fieldWithPath("data.course.content[].courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                                    fieldWithPath("data.course.content[].imageUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                                    fieldWithPath("data.course.content[].title").type(JsonFieldType.STRING).description("코스 제목"),
                                                    fieldWithPath("data.course.content[].subTitle").type(JsonFieldType.STRING).description("코스 부제목"),
                                                    fieldWithPath("data.course.content[].tags").type(JsonFieldType.ARRAY).description("태그 목록"),
                                                    fieldWithPath("data.course.content[].level").type(JsonFieldType.STRING).description("코스 난이도 (BEGINNER/INTERMEDIATE/ADVANCED)"),
                                                    fieldWithPath("data.course.content[].liked").type(JsonFieldType.BOOLEAN).description("찜 여부"),
                                                    fieldWithPath("data.course.lastId").type(JsonFieldType.NUMBER).description("마지막 코스 ID"),
                                                    fieldWithPath("data.course.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                                            )
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("빈 찜 코스 조회 성공")
        @WithMockCustomUser
        void 빈_찜_코스_조회_성공() throws Exception {
            CursorResponse<CourseSummaryResponse> courseList =
                    new CursorResponse<>(List.of(), false);
            MyCourseLikeResponse response = new MyCourseLikeResponse(0L, courseList);

            when(myPageService.getMyCourseLike(eq(1L), eq(3), isNull()))
                    .thenReturn(response);

            mockMvc.perform(get("/api/my/like/course"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(0))
                    .andExpect(jsonPath("$.data.course.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/my/like/place")
    class ViewMyPlaceLike {

        @Test
        @DisplayName("찜한 장소 조회 성공")
        @WithMockCustomUser
        void 찜한_장소_조회_성공() throws Exception {
            PlaceSummaryResponse place = PlaceSummaryResponse.builder()
                    .placeId(1L)
                    .placeName("도봉 카페")
                    .category("카페")
                    .thumbnailUrl("thumb1.jpg")
                    .averageRating(4.5)
                    .reviewCount(10L)
                    .isLiked(true)
                    .latitude(37.6688)
                    .longitude(127.0467)
                    .themes(List.of("NATURE", "HEALING"))
                    .build();
            CursorResponse<PlaceSummaryResponse> placeList =
                    new CursorResponse<>(List.of(place), false);

            MyPlaceLikeResponse response = new MyPlaceLikeResponse(3L, placeList);

            when(myPageService.getMyPlaceLike(eq(1L), eq(3), isNull()))
                    .thenReturn(response);

            mockMvc.perform(get("/api/my/like/place"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.totalCount").value(3))
                    .andExpect(jsonPath("$.data.place.content[0].placeId").value(1))
                    .andExpect(jsonPath("$.data.place.content[0].placeName").value("도봉 카페"))
                    .andExpect(jsonPath("$.data.place.content[0].category").value("카페"))
                    .andExpect(jsonPath("$.data.place.content[0].averageRating").value(4.5))
                    .andExpect(jsonPath("$.data.place.content[0].liked").value(true))
                    .andDo(print())
                    .andDo(document("mypage-like-place",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("마이 페이지 API")
                                            .summary("찜한 장소 조회")
                                            .description("찜한 장소 목록을 조회합니다.")
                                            .queryParameters(
                                                    parameterWithName("lastId").optional()
                                                            .description("커서 - 마지막 장소 ID (첫 요청 시 생략)"),
                                                    parameterWithName("size").optional()
                                                            .description("조회 개수 (기본값: 3)")
                                            )
                                            .responseFields(
                                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),

                                                    fieldWithPath("data.totalCount").type(JsonFieldType.NUMBER).description("찜한 장소 총 개수"),
                                                    fieldWithPath("data.place.content[]").type(JsonFieldType.ARRAY).description("장소 목록"),
                                                    fieldWithPath("data.place.content[].placeId").type(JsonFieldType.NUMBER).description("장소 ID"),
                                                    fieldWithPath("data.place.content[].placeName").type(JsonFieldType.STRING).description("장소 이름"),
                                                    fieldWithPath("data.place.content[].category").type(JsonFieldType.STRING).description("카테고리"),
                                                    fieldWithPath("data.place.content[].thumbnailUrl").type(JsonFieldType.STRING).description("썸네일 URL"),
                                                    fieldWithPath("data.place.content[].averageRating").type(JsonFieldType.NUMBER).description("평균 평점"),
                                                    fieldWithPath("data.place.content[].reviewCount").type(JsonFieldType.NUMBER).description("리뷰 수"),
                                                    fieldWithPath("data.place.content[].liked").type(JsonFieldType.BOOLEAN).description("찜 여부"),
                                                    fieldWithPath("data.place.content[].latitude").type(JsonFieldType.NUMBER).description("위도"),
                                                    fieldWithPath("data.place.content[].longitude").type(JsonFieldType.NUMBER).description("경도"),
                                                    fieldWithPath("data.place.content[].themes").type(JsonFieldType.ARRAY).description("테마 목록"),
                                                    fieldWithPath("data.place.lastId").type(JsonFieldType.NUMBER).description("마지막 장소 ID"),
                                                    fieldWithPath("data.place.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                                            )
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("빈 찜 장소 조회 성공")
        @WithMockCustomUser
        void 빈_찜_장소_조회_성공() throws Exception {
            CursorResponse<PlaceSummaryResponse> placeList =
                    new CursorResponse<>(List.of(), false);
            MyPlaceLikeResponse response = new MyPlaceLikeResponse(0L, placeList);

            when(myPageService.getMyPlaceLike(eq(1L), eq(3), isNull()))
                    .thenReturn(response);

            mockMvc.perform(get("/api/my/like/place"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(0))
                    .andExpect(jsonPath("$.data.place.content").isEmpty());
        }
    }
}
