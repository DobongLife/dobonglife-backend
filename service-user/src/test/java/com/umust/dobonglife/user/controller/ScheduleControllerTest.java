package com.umust.dobonglife.user.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.schedule.application.dto.MonthlyScheduleResult;
import com.umust.dobonglife.domain.schedule.application.dto.UpcomingFestivalDetail;
import com.umust.dobonglife.domain.schedule.application.port.in.FestivalUseCase;
import com.umust.dobonglife.domain.schedule.application.port.in.ScheduleCommandUseCase;
import com.umust.dobonglife.domain.schedule.application.port.in.ScheduleQueryUseCase;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ScheduleController.class, excludeAutoConfiguration = {
        OAuth2ClientAutoConfiguration.class,
        OAuth2ClientWebSecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class
})
@Import(TestWebMvcConfig.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class ScheduleControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean ScheduleCommandUseCase scheduleCommandUseCase;
    @MockitoBean ScheduleQueryUseCase scheduleQueryUseCase;
    @MockitoBean FestivalUseCase festivalUseCase;
    @MockitoBean TokenExtractor tokenExtractor;
    @MockitoBean AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;

    @Test
    @DisplayName("일정 등록 - 성공")
    @WithMockCustomUser
    void registerSchedule_success() throws Exception {
        willDoNothing().given(scheduleCommandUseCase).registerSchedule(
                anyString(), any(), any(), anyString(), anyString(), anyBoolean(), anyBoolean(), anyString(), anyLong());

        mockMvc.perform(post("/api/schedules").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "도봉산 등산",
                                    "startTime": "2026-04-10T09:00:00",
                                    "endTime": "2026-04-10T12:00:00",
                                    "memo": "등산 준비물 챙기기",
                                    "placeName": "도봉산",
                                    "isEvent": false,
                                    "isAllDay": false,
                                    "color": "#FF5733"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("schedule-register",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("일정 API").summary("일정 등록")
                                .description("새로운 일정을 등록합니다.")
                                .requestFields(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("일정 제목"),
                                        fieldWithPath("startTime").type(JsonFieldType.STRING).description("시작 시간"),
                                        fieldWithPath("endTime").type(JsonFieldType.STRING).description("종료 시간"),
                                        fieldWithPath("memo").type(JsonFieldType.STRING).description("메모"),
                                        fieldWithPath("placeName").type(JsonFieldType.STRING).description("장소명"),
                                        fieldWithPath("isEvent").type(JsonFieldType.BOOLEAN).description("이벤트 여부"),
                                        fieldWithPath("isAllDay").type(JsonFieldType.BOOLEAN).description("종일 여부"),
                                        fieldWithPath("color").type(JsonFieldType.STRING).description("색상"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(scheduleCommandUseCase).registerSchedule(
                eq("도봉산 등산"), any(LocalDateTime.class), any(LocalDateTime.class),
                eq("등산 준비물 챙기기"), eq("도봉산"), eq(false), eq(false), eq("#FF5733"), eq(1L));
    }

    @Test
    @DisplayName("월별 일정 조회 - 성공")
    @WithMockCustomUser
    void getMonthlySchedule_success() throws Exception {
        MonthlyScheduleResult result = MonthlyScheduleResult.from(Collections.emptyList());
        given(scheduleQueryUseCase.getMonthlyScheduleList(anyLong(), anyInt(), anyInt())).willReturn(result);

        mockMvc.perform(get("/api/schedules/monthly")
                        .param("year", "2026")
                        .param("month", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andDo(print())
                .andDo(document("schedule-monthly",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("일정 API").summary("월별 일정 조회")
                                .description("해당 월의 일정 목록을 조회합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("월별 일정 데이터"),
                                        fieldWithPath("data.scheduleList").type(JsonFieldType.ARRAY).description("일정 목록"))
                                .build())
                ));

        verify(scheduleQueryUseCase).getMonthlyScheduleList(1L, 2026, 4);
    }

    @Test
    @DisplayName("일정 삭제 - 성공")
    @WithMockCustomUser
    void deleteSchedule_success() throws Exception {
        willDoNothing().given(scheduleCommandUseCase).deleteSchedule(anyLong(), anyLong());

        mockMvc.perform(delete("/api/schedules/{scheduleId}", 10L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("schedule-delete",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("일정 API").summary("일정 삭제")
                                .description("일정을 삭제합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(scheduleCommandUseCase).deleteSchedule(10L, 1L);
    }

    @Test
    @DisplayName("일정 수정 - 성공")
    @WithMockCustomUser
    void updateSchedule_success() throws Exception {
        willDoNothing().given(scheduleCommandUseCase).updateSchedule(
                anyLong(), anyString(), any(), any(), anyString(), anyString(), anyBoolean(), anyBoolean(), anyString(), anyLong());

        mockMvc.perform(patch("/api/schedules/{scheduleId}", 10L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "수정된 일정",
                                    "startTime": "2026-04-11T10:00:00",
                                    "endTime": "2026-04-11T13:00:00",
                                    "memo": "수정된 메모",
                                    "placeName": "수락산",
                                    "isEvent": true,
                                    "isAllDay": false,
                                    "color": "#00FF00"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print())
                .andDo(document("schedule-update",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("일정 API").summary("일정 수정")
                                .description("일정을 수정합니다.")
                                .requestFields(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("일정 제목"),
                                        fieldWithPath("startTime").type(JsonFieldType.STRING).description("시작 시간"),
                                        fieldWithPath("endTime").type(JsonFieldType.STRING).description("종료 시간"),
                                        fieldWithPath("memo").type(JsonFieldType.STRING).description("메모"),
                                        fieldWithPath("placeName").type(JsonFieldType.STRING).description("장소명"),
                                        fieldWithPath("isEvent").type(JsonFieldType.BOOLEAN).description("이벤트 여부"),
                                        fieldWithPath("isAllDay").type(JsonFieldType.BOOLEAN).description("종일 여부"),
                                        fieldWithPath("color").type(JsonFieldType.STRING).description("색상"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (없음)"))
                                .build())
                ));

        verify(scheduleCommandUseCase).updateSchedule(
                eq(10L), eq("수정된 일정"), any(LocalDateTime.class), any(LocalDateTime.class),
                eq("수정된 메모"), eq("수락산"), eq(true), eq(false), eq("#00FF00"), eq(1L));
    }

    @Test
    @DisplayName("다가오는 축제 조회 - 성공")
    void getUpcomingFestivals_success() throws Exception {
        List<UpcomingFestivalDetail> festivals = List.of(
                UpcomingFestivalDetail.builder()
                        .festivalId(1L)
                        .title("도봉 봄꽃 축제")
                        .placeName("도봉구 문화센터")
                        .startTime(LocalDateTime.of(2026, 4, 15, 10, 0))
                        .endTime(LocalDateTime.of(2026, 4, 15, 18, 0))
                        .url("https://example.com/festival/1")
                        .category("축제")
                        .build()
        );
        given(festivalUseCase.getUpcomingTop3()).willReturn(festivals);

        mockMvc.perform(get("/api/schedules/festivals/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].festivalId").value(1))
                .andExpect(jsonPath("$.data[0].title").value("도봉 봄꽃 축제"))
                .andDo(print())
                .andDo(document("schedule-festivals-upcoming",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("일정 API").summary("다가오는 축제 조회")
                                .description("다가오는 축제 상위 3건을 조회합니다.")
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("축제 목록"),
                                        fieldWithPath("data[].festivalId").type(JsonFieldType.NUMBER).description("축제 ID"),
                                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("축제 제목"),
                                        fieldWithPath("data[].placeName").type(JsonFieldType.STRING).description("장소명"),
                                        fieldWithPath("data[].startTime").type(JsonFieldType.STRING).description("시작 시간"),
                                        fieldWithPath("data[].endTime").type(JsonFieldType.STRING).description("종료 시간"),
                                        fieldWithPath("data[].url").type(JsonFieldType.STRING).description("URL"),
                                        fieldWithPath("data[].category").type(JsonFieldType.STRING).description("카테고리"))
                                .build())
                ));

        verify(festivalUseCase).getUpcomingTop3();
    }
}
