package com.umust.dobonglife.domain.courseLike.controller;

import com.umust.dobonglife.domain.courseLike.controller.dto.request.CourseLikeResponse;
import com.umust.dobonglife.domain.courseLike.service.CourseLikeService;
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

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class CourseLikeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CourseLikeService courseLikeService;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Nested
    @DisplayName("POST /api/course/like/{courseId}")
    class UpdateCourseLike {

        @Test
        @DisplayName("코스 찜하기 성공")
        @WithMockCustomUser
        void 코스_찜하기_성공() throws Exception {
            CourseLikeResponse response = CourseLikeResponse.from(1L, 10L, true);

            when(courseLikeService.updateCourseLike(eq(10L), eq(1L)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/course/like/{courseId}", 10L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.userId").value(1))
                    .andExpect(jsonPath("$.data.courseId").value(10))
                    .andExpect(jsonPath("$.data.isFavorite").value(true))
                    .andDo(print())
                    .andDo(document("course-like-update",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Course Like")
                                            .summary("코스 찜하기")
                                            .description("코스를 찜하거나 해제합니다.")
                                            .pathParameters(
                                                    parameterWithName("courseId").description("코스 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                    fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                                    fieldWithPath("data.courseId").type(JsonFieldType.NUMBER).description("코스 ID"),
                                                    fieldWithPath("data.isFavorite").type(JsonFieldType.BOOLEAN).description("찜 여부 (true: 찜 추가, false: 찜 해제)")
                                            )
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("코스 찜 해제 성공")
        @WithMockCustomUser
        void 코스_찜_해제_성공() throws Exception {
            CourseLikeResponse response = CourseLikeResponse.from(1L, 10L, false);

            when(courseLikeService.updateCourseLike(eq(10L), eq(1L)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/course/like/{courseId}", 10L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.userId").value(1))
                    .andExpect(jsonPath("$.data.courseId").value(10))
                    .andExpect(jsonPath("$.data.isFavorite").value(false));
        }
    }
}
