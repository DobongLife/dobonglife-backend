package com.umust.dobonglife.domain.schedule.service;

import com.umust.dobonglife.domain.schedule.controller.dto.request.ScheduleRequest;
import com.umust.dobonglife.domain.schedule.controller.dto.response.MonthlyScheduleListResponse;
import com.umust.dobonglife.domain.schedule.domain.constant.Color;
import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;
import com.umust.dobonglife.domain.schedule.domain.repository.ScheduleRepository;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private UserRepository userRepository;

    private User createTestUser(Long id) {
        User user = mock(User.class);
        return user;
    }

    private Schedule createTestSchedule(Long id, User user) {
        Schedule schedule = Schedule.builder()
                .title("독서 회의")
                .startTime(LocalDateTime.of(2025, 1, 10, 14, 0))
                .endTime(LocalDateTime.of(2025, 1, 10, 16, 0))
                .memo("노트북 챙겨가기")
                .placeName("도봉구 도서관")
                .isEvent(false)
                .isAllDay(false)
                .color(Color.RED)
                .user(user)
                .build();
        ReflectionTestUtils.setField(schedule, "id", id);
        return schedule;
    }

    private ScheduleRequest createScheduleRequest(String title, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            var ctor = ScheduleRequest.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            ScheduleRequest request = ctor.newInstance();
            ReflectionTestUtils.setField(request, "title", title);
            ReflectionTestUtils.setField(request, "startTime", startTime);
            ReflectionTestUtils.setField(request, "endTime", endTime);
            ReflectionTestUtils.setField(request, "memo", "메모");
            ReflectionTestUtils.setField(request, "placeName", "도봉구 도서관");
            ReflectionTestUtils.setField(request, "isEvent", false);
            ReflectionTestUtils.setField(request, "isAllDay", false);
            ReflectionTestUtils.setField(request, "color", "RED");
            return request;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("registerSchedule - 일정 등록")
    class RegisterSchedule {

        @Test
        @DisplayName("일정 등록 성공")
        void success() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            ScheduleRequest request = createScheduleRequest(
                    "독서 회의",
                    LocalDateTime.of(2025, 1, 10, 14, 0),
                    LocalDateTime.of(2025, 1, 10, 16, 0)
            );

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(scheduleRepository.save(any(Schedule.class))).willAnswer(invocation -> {
                Schedule s = invocation.getArgument(0);
                ReflectionTestUtils.setField(s, "id", 1L);
                return s;
            });

            // when
            scheduleService.registerSchedule(request, userId);

            // then
            then(scheduleRepository).should().save(any(Schedule.class));
        }

        @Test
        @DisplayName("사용자가 없으면 USER_NOT_FOUND 예외")
        void userNotFound() {
            // given
            Long userId = 999L;
            ScheduleRequest request = createScheduleRequest(
                    "독서 회의",
                    LocalDateTime.of(2025, 1, 10, 14, 0),
                    LocalDateTime.of(2025, 1, 10, 16, 0)
            );

            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> scheduleService.registerSchedule(request, userId))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("종료시간이 시작시간보다 이전이면 END_TIME_BEFORE_START_TIME 예외")
        void endTimeBeforeStartTime() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            ScheduleRequest request = createScheduleRequest(
                    "독서 회의",
                    LocalDateTime.of(2025, 1, 10, 16, 0),
                    LocalDateTime.of(2025, 1, 10, 14, 0)
            );

            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when & then
            assertThatThrownBy(() -> scheduleService.registerSchedule(request, userId))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.END_TIME_BEFORE_START_TIME);
        }
    }

    @Nested
    @DisplayName("deleteSchedule - 일정 삭제")
    class DeleteSchedule {

        @Test
        @DisplayName("일정 삭제 성공")
        void success() {
            // given
            Long userId = 1L;
            Long scheduleId = 1L;
            User user = createTestUser(userId);
            Schedule schedule = createTestSchedule(scheduleId, user);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(scheduleRepository.findByIdAndUserId(scheduleId, userId))
                    .willReturn(Optional.of(schedule));

            // when
            scheduleService.deleteSchedule(scheduleId, userId);

            // then
            then(scheduleRepository).should().delete(schedule);
        }

        @Test
        @DisplayName("일정이 없으면 SCHEDULE_NOT_FOUND 예외")
        void scheduleNotFound() {
            // given
            Long userId = 1L;
            Long scheduleId = 999L;
            User user = createTestUser(userId);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(scheduleRepository.findByIdAndUserId(scheduleId, userId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> scheduleService.deleteSchedule(scheduleId, userId))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.SCHEDULE_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("updateSchedule - 일정 수정")
    class UpdateSchedule {

        @Test
        @DisplayName("일정 수정 성공")
        void success() {
            // given
            Long userId = 1L;
            Long scheduleId = 1L;
            User user = createTestUser(userId);
            Schedule schedule = createTestSchedule(scheduleId, user);

            ScheduleRequest request = createScheduleRequest(
                    "수정된 회의",
                    LocalDateTime.of(2025, 1, 10, 15, 0),
                    LocalDateTime.of(2025, 1, 10, 17, 0)
            );

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(scheduleRepository.findByIdAndUserId(scheduleId, userId))
                    .willReturn(Optional.of(schedule));

            // when
            scheduleService.updateSchedule(scheduleId, request, userId);

            // then
            assertThat(schedule.getTitle()).isEqualTo("수정된 회의");
            assertThat(schedule.getStartTime()).isEqualTo(LocalDateTime.of(2025, 1, 10, 15, 0));
        }

        @Test
        @DisplayName("일정이 없으면 SCHEDULE_NOT_FOUND 예외")
        void scheduleNotFound() {
            // given
            Long userId = 1L;
            Long scheduleId = 999L;
            User user = createTestUser(userId);

            ScheduleRequest request = createScheduleRequest(
                    "수정된 회의",
                    LocalDateTime.of(2025, 1, 10, 15, 0),
                    LocalDateTime.of(2025, 1, 10, 17, 0)
            );

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(scheduleRepository.findByIdAndUserId(scheduleId, userId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> scheduleService.updateSchedule(scheduleId, request, userId))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.SCHEDULE_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("getMonthlyScheduleList - 월간 일정 조회")
    class GetMonthlyScheduleList {

        @Test
        @DisplayName("멀티데이 일정 포함 조회 성공")
        void successWithMultiDay() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);

            // 단일 날짜 일정
            Schedule singleDay = createTestSchedule(1L, user);

            // 멀티데이 일정 (1월 9일 ~ 1월 11일)
            Schedule multiDay = Schedule.builder()
                    .title("워크숍")
                    .startTime(LocalDateTime.of(2025, 1, 9, 9, 0))
                    .endTime(LocalDateTime.of(2025, 1, 11, 18, 0))
                    .memo("3일간 워크숍")
                    .placeName("연수원")
                    .isEvent(false)
                    .isAllDay(false)
                    .color(Color.BLUE)
                    .user(user)
                    .build();
            ReflectionTestUtils.setField(multiDay, "id", 2L);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(scheduleRepository.findMonthlyOverlaps(eq(userId), any(), any()))
                    .willReturn(List.of(singleDay, multiDay));

            // when
            MonthlyScheduleListResponse result = scheduleService.getMonthlyScheduleList(userId, 2025, 1);

            // then
            assertThat(result.getScheduleList()).isNotEmpty();
            // 멀티데이 일정은 여러 날짜에 걸쳐 표시되어야 함
            long totalScheduleEntries = result.getScheduleList().stream()
                    .mapToLong(daily -> daily.getSchedules().size())
                    .sum();
            assertThat(totalScheduleEntries).isGreaterThan(2);
        }

        @Test
        @DisplayName("빈 결과 반환")
        void emptyResult() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(scheduleRepository.findMonthlyOverlaps(eq(userId), any(), any()))
                    .willReturn(List.of());

            // when
            MonthlyScheduleListResponse result = scheduleService.getMonthlyScheduleList(userId, 2025, 1);

            // then
            assertThat(result.getScheduleList()).isEmpty();
        }
    }
}
