package com.umust.dobonglife.domain.schedule.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.domain.schedule.controller.dto.response.DailyScheduleResponse;
import com.umust.dobonglife.domain.schedule.controller.dto.response.MonthlyScheduleListResponse;
import com.umust.dobonglife.domain.schedule.controller.dto.response.ScheduleResponse;
import com.umust.dobonglife.domain.schedule.controller.dto.response.UpcomingFestivalResponse;
import com.umust.dobonglife.domain.schedule.service.FestivalService;
import com.umust.dobonglife.domain.schedule.service.ScheduleService;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ScheduleService scheduleService;

    @MockitoBean
    private FestivalService festivalService;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Nested
    @DisplayName("POST /api/schedules - 일정 등록")
    class RegisterSchedule {

        @Test
        @WithMockCustomUser
        @DisplayName("일정 등록 성공 200")
        void success() throws Exception {
            // given
            willDoNothing().given(scheduleService).registerSchedule(any(), any());

            String requestJson = """
                    {
                        "title": "독서 회의",
                        "startTime": "2025-01-10T14:00:00",
                        "endTime": "2025-01-10T16:00:00",
                        "memo": "노트북 챙겨가기",
                        "placeName": "도봉구 도서관",
                        "isEvent": false,
                        "isAllDay": false,
                        "color": "RED"
                    }
                    """;

            // when & then
            mockMvc.perform(post("/api/schedules")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andDo(print())
                    .andDo(document("schedule-register",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Schedule")
                                    .summary("일정 등록")
                                    .description("새로운 일정을 등록합니다.")
                                    .requestFields(
                                            fieldWithPath("title").type(JsonFieldType.STRING).description("일정 제목"),
                                            fieldWithPath("startTime").type(JsonFieldType.STRING).description("시작 시간 (yyyy-MM-dd'T'HH:mm:ss)"),
                                            fieldWithPath("endTime").type(JsonFieldType.STRING).description("종료 시간 (yyyy-MM-dd'T'HH:mm:ss)"),
                                            fieldWithPath("memo").type(JsonFieldType.STRING).description("메모"),
                                            fieldWithPath("placeName").type(JsonFieldType.STRING).description("장소 이름"),
                                            fieldWithPath("isEvent").type(JsonFieldType.BOOLEAN).description("이벤트 여부"),
                                            fieldWithPath("isAllDay").type(JsonFieldType.BOOLEAN).description("하루 종일 여부"),
                                            fieldWithPath("color").type(JsonFieldType.STRING).description("일정 색깔 (RED, ORANGE, YELLOW, GREEN, BLUE, BROWN, PINK)")
                                    )
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)")
                                    )
                                    .build())
                    ));
        }
    }

    @Nested
    @DisplayName("GET /api/schedules/monthly - 월간 일정 조회")
    class GetMonthlySchedule {

        @Test
        @WithMockCustomUser
        @DisplayName("월간 일정 조회 성공 200")
        void success() throws Exception {
            // given
            ScheduleResponse scheduleResponse = ScheduleResponse.builder()
                    .id(1L)
                    .title("독서 회의")
                    .startTime(LocalDateTime.of(2025, 1, 10, 14, 0))
                    .endTime(LocalDateTime.of(2025, 1, 10, 16, 0))
                    .memo("노트북 챙겨가기")
                    .placeName("도봉구 도서관")
                    .isAllDay(false)
                    .isEvent(false)
                    .color("RED")
                    .build();

            DailyScheduleResponse dailyResponse = DailyScheduleResponse.of(
                    LocalDate.of(2025, 1, 10),
                    List.of(scheduleResponse)
            );

            MonthlyScheduleListResponse monthlyResponse =
                    MonthlyScheduleListResponse.from(List.of(dailyResponse));

            given(scheduleService.getMonthlyScheduleList(any(), eq(2025), eq(1)))
                    .willReturn(monthlyResponse);

            // when & then
            mockMvc.perform(get("/api/schedules/monthly")
                            .param("year", "2025")
                            .param("month", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.scheduleList").isArray())
                    .andExpect(jsonPath("$.data.scheduleList[0].date").value("2025-01-10"))
                    .andExpect(jsonPath("$.data.scheduleList[0].schedules[0].title").value("독서 회의"))
                    .andDo(print())
                    .andDo(document("schedule-monthly",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Schedule")
                                    .summary("월별 일정 조회")
                                    .description("해당 월의 일정 목록을 조회합니다.")
                                    .queryParameters(
                                            parameterWithName("year").description("조회 연도"),
                                            parameterWithName("month").description("조회 월")
                                    )
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data.scheduleList[]").type(JsonFieldType.ARRAY).description("일별 일정 목록"),
                                            fieldWithPath("data.scheduleList[].date").type(JsonFieldType.STRING).description("날짜 (yyyy-MM-dd)"),
                                            fieldWithPath("data.scheduleList[].schedules[]").type(JsonFieldType.ARRAY).description("해당 날짜 일정 목록"),
                                            fieldWithPath("data.scheduleList[].schedules[].id").type(JsonFieldType.NUMBER).description("일정 ID"),
                                            fieldWithPath("data.scheduleList[].schedules[].title").type(JsonFieldType.STRING).description("일정 제목"),
                                            fieldWithPath("data.scheduleList[].schedules[].startTime").type(JsonFieldType.STRING).description("시작 시간"),
                                            fieldWithPath("data.scheduleList[].schedules[].endTime").type(JsonFieldType.STRING).description("종료 시간"),
                                            fieldWithPath("data.scheduleList[].schedules[].memo").type(JsonFieldType.STRING).description("메모"),
                                            fieldWithPath("data.scheduleList[].schedules[].placeName").type(JsonFieldType.STRING).description("장소 이름"),
                                            fieldWithPath("data.scheduleList[].schedules[].isAllDay").type(JsonFieldType.BOOLEAN).description("하루 종일 여부"),
                                            fieldWithPath("data.scheduleList[].schedules[].isEvent").type(JsonFieldType.BOOLEAN).description("이벤트 여부"),
                                            fieldWithPath("data.scheduleList[].schedules[].color").type(JsonFieldType.STRING).description("일정 색깔")
                                    )
                                    .build())
                    ));
        }
    }

    @Nested
    @DisplayName("PATCH /api/schedules/{scheduleId} - 일정 수정")
    class UpdateSchedule {

        @Test
        @WithMockCustomUser
        @DisplayName("일정 수정 성공 200")
        void success() throws Exception {
            // given
            willDoNothing().given(scheduleService).updateSchedule(any(), any(), any());

            String requestJson = """
                    {
                        "title": "수정된 회의",
                        "startTime": "2025-01-10T15:00:00",
                        "endTime": "2025-01-10T17:00:00",
                        "memo": "수정된 메모",
                        "placeName": "도봉구 시청",
                        "isEvent": false,
                        "isAllDay": false,
                        "color": "BLUE"
                    }
                    """;

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/schedules/{scheduleId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andDo(print())
                    .andDo(document("schedule-update",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Schedule")
                                    .summary("일정 수정")
                                    .description("기존 일정을 수정합니다.")
                                    .pathParameters(
                                            parameterWithName("scheduleId").description("일정 고유 ID")
                                    )
                                    .requestFields(
                                            fieldWithPath("title").type(JsonFieldType.STRING).description("일정 제목"),
                                            fieldWithPath("startTime").type(JsonFieldType.STRING).description("시작 시간"),
                                            fieldWithPath("endTime").type(JsonFieldType.STRING).description("종료 시간"),
                                            fieldWithPath("memo").type(JsonFieldType.STRING).description("메모"),
                                            fieldWithPath("placeName").type(JsonFieldType.STRING).description("장소 이름"),
                                            fieldWithPath("isEvent").type(JsonFieldType.BOOLEAN).description("이벤트 여부"),
                                            fieldWithPath("isAllDay").type(JsonFieldType.BOOLEAN).description("하루 종일 여부"),
                                            fieldWithPath("color").type(JsonFieldType.STRING).description("일정 색깔")
                                    )
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)")
                                    )
                                    .build())
                    ));
        }
    }

    @Nested
    @DisplayName("DELETE /api/schedules/{scheduleId} - 일정 삭제")
    class DeleteSchedule {

        @Test
        @WithMockCustomUser
        @DisplayName("일정 삭제 성공 200")
        void success() throws Exception {
            // given
            willDoNothing().given(scheduleService).deleteSchedule(any(), any());

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/schedules/{scheduleId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andDo(print())
                    .andDo(document("schedule-delete",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Schedule")
                                    .summary("일정 삭제")
                                    .description("일정을 삭제합니다.")
                                    .pathParameters(
                                            parameterWithName("scheduleId").description("일정 고유 ID")
                                    )
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)")
                                    )
                                    .build())
                    ));
        }
    }

    @Nested
    @DisplayName("GET /api/schedules/festivals/upcoming - 다가오는 축제 조회")
    class GetUpcomingFestivals {

        @Test
        @WithMockCustomUser
        @DisplayName("다가오는 축제 조회 성공 200")
        void success() throws Exception {
            // given
            List<UpcomingFestivalResponse> festivals = List.of(
                    UpcomingFestivalResponse.builder()
                            .festivalId(1L)
                            .title("도봉구 벚꽃 축제")
                            .placeName("도봉산")
                            .startTime(LocalDateTime.of(2025, 4, 1, 10, 0))
                            .endTime(LocalDateTime.of(2025, 4, 5, 18, 0))
                            .url("https://example.com/festival/1")
                            .category("축제")
                            .build()
            );

            given(festivalService.getUpcomingTop3()).willReturn(festivals);

            // when & then
            mockMvc.perform(get("/api/schedules/festivals/upcoming"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data[0].festivalId").value(1))
                    .andExpect(jsonPath("$.data[0].title").value("도봉구 벚꽃 축제"))
                    .andDo(print())
                    .andDo(document("schedule-festivals-upcoming",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Schedule")
                                    .summary("다가오는 축제 조회")
                                    .description("다가오는 축제 목록을 조회합니다.")
                                    .responseFields(
                                            fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                            fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                            fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("다가오는 축제 목록"),
                                            fieldWithPath("data[].festivalId").type(JsonFieldType.NUMBER).description("축제 ID"),
                                            fieldWithPath("data[].title").type(JsonFieldType.STRING).description("축제 제목"),
                                            fieldWithPath("data[].placeName").type(JsonFieldType.STRING).description("축제 장소"),
                                            fieldWithPath("data[].startTime").type(JsonFieldType.STRING).description("시작 시간"),
                                            fieldWithPath("data[].endTime").type(JsonFieldType.STRING).description("종료 시간"),
                                            fieldWithPath("data[].url").type(JsonFieldType.STRING).description("축제 상세 URL"),
                                            fieldWithPath("data[].category").type(JsonFieldType.STRING).description("축제 카테고리")
                                    )
                                    .build())
                    ));
        }
    }
}
