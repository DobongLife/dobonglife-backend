package com.umust.dobonglife.domain.notification.controller;

import com.umust.dobonglife.domain.notification.domain.constant.NotificationType;
import com.umust.dobonglife.domain.notification.presentation.dto.response.NotificationResponse;
import com.umust.dobonglife.domain.notification.service.NotificationService;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.response.CursorResponse;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class NotificationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    NotificationService notificationService;

    @MockitoBean
    UserService userService;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Nested
    @DisplayName("GET /api/notifications")
    class GetNotifications {

        @Test
        @DisplayName("알림 목록 조회 성공")
        @WithMockCustomUser
        void 알림_목록_조회_성공() throws Exception {
            NotificationResponse notification = new NotificationResponse(
                    1L, NotificationType.POINT, "포인트 적립 안내",
                    "10포인트가 적립되었습니다!", false,
                    LocalDateTime.of(2025, 6, 1, 12, 0), "3시간 전", null
            );
            CursorResponse<NotificationResponse> response =
                    new CursorResponse<>(List.of(notification), false);

            when(notificationService.getNotifications(eq(1L), eq("ALL"), isNull(), eq(2)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/notifications"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content[0].notificationId").value(1))
                    .andExpect(jsonPath("$.data.content[0].type").value("POINT"))
                    .andExpect(jsonPath("$.data.content[0].title").value("포인트 적립 안내"))
                    .andExpect(jsonPath("$.data.content[0].content").value("10포인트가 적립되었습니다!"))
                    .andExpect(jsonPath("$.data.content[0].timeAgo").value("3시간 전"))
                    .andDo(print())
                    .andDo(document("notification-list",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("알림 API")
                                            .summary("알림 목록 조회")
                                            .description("알림 목록을 필터별로 조회합니다.")
                                            .queryParameters(
                                                    parameterWithName("filter").optional()
                                                            .description("알림 필터 (ALL, UNREAD, POINT, SCHEDULE, COURSE, COUPON / 기본값: ALL)"),
                                                    parameterWithName("lastId").optional()
                                                            .description("커서 - 마지막 알림 ID (첫 요청 시 생략)"),
                                                    parameterWithName("size").optional()
                                                            .description("조회 개수 (기본값: 2)")
                                            )
                                            .responseFields(
                                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                    fieldWithPath("data.content[]").type(JsonFieldType.ARRAY).description("알림 목록"),
                                                    fieldWithPath("data.content[].notificationId").type(JsonFieldType.NUMBER).description("알림 ID"),
                                                    fieldWithPath("data.content[].type").type(JsonFieldType.STRING).description("알림 유형 (POINT, SCHEDULE, COURSE, COUPON)"),
                                                    fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("알림 제목"),
                                                    fieldWithPath("data.content[].content").type(JsonFieldType.STRING).description("알림 내용"),
                                                    fieldWithPath("data.content[].isRead").type(JsonFieldType.BOOLEAN).description("읽음 여부"),
                                                    fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING).description("생성 일시"),
                                                    fieldWithPath("data.content[].timeAgo").type(JsonFieldType.STRING).description("경과 시간 (예: 3시간 전)"),
                                                    fieldWithPath("data.content[].relatedUrlId").type(JsonFieldType.NULL).description("관련 URL ID").optional(),
                                                    fieldWithPath("data.lastId").type(JsonFieldType.NUMBER).description("마지막 알림 ID"),
                                                    fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN).description("다음 페이지 여부")
                                            )
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("필터 파라미터 전달 성공")
        @WithMockCustomUser
        void 필터_파라미터_전달_성공() throws Exception {
            CursorResponse<NotificationResponse> response =
                    new CursorResponse<>(List.of(), false);

            when(notificationService.getNotifications(eq(1L), eq("POINT"), isNull(), eq(2)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/notifications")
                            .param("filter", "POINT"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty());
        }

        @Test
        @DisplayName("커서 파라미터 전달 성공")
        @WithMockCustomUser
        void 커서_파라미터_전달_성공() throws Exception {
            CursorResponse<NotificationResponse> response =
                    new CursorResponse<>(List.of(), false);

            when(notificationService.getNotifications(eq(1L), eq("ALL"), eq(5L), eq(2)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/notifications")
                            .param("lastId", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/notifications/new")
    class CheckNewNotifications {

        @Test
        @DisplayName("새 알림 있음")
        @WithMockCustomUser
        void 새_알림_있음() throws Exception {
            when(notificationService.hasNewNotifications(eq(1L)))
                    .thenReturn(true);

            mockMvc.perform(get("/api/notifications/new"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("true"))
                    .andDo(print())
                    .andDo(document("notification-check-new",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("알림 API")
                                            .summary("새 알림 확인")
                                            .description("새로운 알림이 있는지 확인합니다.")
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("새 알림 없음")
        @WithMockCustomUser
        void 새_알림_없음() throws Exception {
            when(notificationService.hasNewNotifications(eq(1L)))
                    .thenReturn(false);

            mockMvc.perform(get("/api/notifications/new"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("false"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/notifications/{notificationId}/read")
    class MarkAsRead {

        @Test
        @DisplayName("알림 읽음 처리 성공")
        @WithMockCustomUser
        void 알림_읽음_처리_성공() throws Exception {
            doNothing().when(notificationService).markAsRead(eq(1L), eq(1L));

            mockMvc.perform(patch("/api/notifications/{notificationId}/read", 1L))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andDo(document("notification-mark-read",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("알림 API")
                                            .summary("알림 읽음 처리")
                                            .description("알림을 읽음 처리합니다.")
                                            .pathParameters(
                                                    parameterWithName("notificationId").description("읽음 처리할 알림 ID")
                                            )
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("PATCH /api/notifications/settings/notification")
    class ToggleNotification {

        @Test
        @DisplayName("알림 설정 활성화 성공")
        @WithMockCustomUser
        void 알림_설정_활성화_성공() throws Exception {
            doNothing().when(userService).updateNotificationSetting(eq(1L), eq(true));

            mockMvc.perform(patch("/api/notifications/settings/notification?enabled=true"))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("notification-toggle-setting",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("알림 API")
                                            .summary("알림 설정 변경")
                                            .description("알림 수신 설정을 변경합니다.")
                                            .queryParameters(
                                                    parameterWithName("enabled").description("알림 수신 여부 (true/false)")
                                            )
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("알림 설정 비활성화 성공")
        @WithMockCustomUser
        void 알림_설정_비활성화_성공() throws Exception {
            doNothing().when(userService).updateNotificationSetting(eq(1L), eq(false));

            mockMvc.perform(patch("/api/notifications/settings/notification?enabled=false"))
                    .andExpect(status().isOk());
        }
    }
}
