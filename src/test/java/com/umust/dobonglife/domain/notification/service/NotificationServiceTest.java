package com.umust.dobonglife.domain.notification.service;

import com.umust.dobonglife.domain.notification.domain.constant.NotificationType;
import com.umust.dobonglife.domain.notification.domain.entity.Notification;
import com.umust.dobonglife.domain.notification.domain.repository.NotificationRepository;
import com.umust.dobonglife.domain.notification.exception.NotificationException;
import com.umust.dobonglife.domain.notification.presentation.dto.response.NotificationResponse;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.external.firebase.NotificationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    NotificationRepository notificationRepository;

    @Mock
    NotificationUtil notificationUtil;

    @InjectMocks
    NotificationService notificationService;

    private Notification createTestNotification(Long id, User user, NotificationType type) {
        Notification notification = Notification.create(user, type, "알림 제목", "알림 내용", null);
        ReflectionTestUtils.setField(notification, "id", id);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());
        return notification;
    }

    private User createTestUser(Long id) {
        return User.builder()
                .id(id)
                .name("테스트유저")
                .fcmToken("test-fcm-token")
                .build();
    }

    @Nested
    @DisplayName("getNotifications - 알림 조회")
    class GetNotifications {

        @Test
        @DisplayName("전체 조회")
        void 전체_조회() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Notification notification = createTestNotification(1L, user, NotificationType.POINT);
            SliceImpl<Notification> slice = new SliceImpl<>(List.of(notification));

            given(notificationRepository.findNotificationsNoOffset(eq(userId), isNull(), isNull(), any(Pageable.class)))
                    .willReturn(slice);

            // when
            CursorResponse<NotificationResponse> result = notificationService.getNotifications(userId, "ALL", null, 10);

            // then
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("타입 필터")
        void 타입_필터() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Notification notification = createTestNotification(1L, user, NotificationType.POINT);
            SliceImpl<Notification> slice = new SliceImpl<>(List.of(notification));

            given(notificationRepository.findNotificationsNoOffset(eq(userId), isNull(), eq(NotificationType.POINT), any(Pageable.class)))
                    .willReturn(slice);

            // when
            CursorResponse<NotificationResponse> result = notificationService.getNotifications(userId, "POINT", null, 10);

            // then
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("유효하지 않은 필터는 전체 조회")
        void 유효하지_않은_필터는_전체_조회() {
            // given
            Long userId = 1L;
            SliceImpl<Notification> emptySlice = new SliceImpl<>(List.of());

            given(notificationRepository.findNotificationsNoOffset(eq(userId), isNull(), isNull(), any(Pageable.class)))
                    .willReturn(emptySlice);

            // when
            CursorResponse<NotificationResponse> result = notificationService.getNotifications(userId, "INVALID", null, 10);

            // then
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("빈 결과")
        void 빈_결과() {
            // given
            Long userId = 1L;
            SliceImpl<Notification> emptySlice = new SliceImpl<>(List.of());

            given(notificationRepository.findNotificationsNoOffset(eq(userId), isNull(), isNull(), any(Pageable.class)))
                    .willReturn(emptySlice);

            // when
            CursorResponse<NotificationResponse> result = notificationService.getNotifications(userId, null, null, 10);

            // then
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("markAsRead - 읽음 처리")
    class MarkAsRead {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Long userId = 1L;
            User user = createTestUser(userId);
            Notification notification = createTestNotification(1L, user, NotificationType.POINT);

            given(notificationRepository.findById(1L)).willReturn(Optional.of(notification));

            // when
            notificationService.markAsRead(1L, userId);

            // then
            assertThat(notification.isRead()).isTrue();
        }

        @Test
        @DisplayName("알림 없음 예외")
        void 알림_없음_예외() {
            // given
            given(notificationRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> notificationService.markAsRead(999L, 1L))
                    .isInstanceOf(NotificationException.class)
                    .extracting(e -> ((NotificationException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_NOTIFICATION_ID);
        }

        @Test
        @DisplayName("소유자 아님 예외")
        void 소유자_아님_예외() {
            // given
            User owner = createTestUser(1L);
            Notification notification = createTestNotification(1L, owner, NotificationType.POINT);

            given(notificationRepository.findById(1L)).willReturn(Optional.of(notification));

            // when & then
            assertThatThrownBy(() -> notificationService.markAsRead(1L, 2L))
                    .isInstanceOf(NotificationException.class)
                    .extracting(e -> ((NotificationException) e).getErrorCode())
                    .isEqualTo(ErrorCode.FORBIDDEN_USER_ID);
        }
    }

    @Nested
    @DisplayName("hasNewNotifications - 새 알림 확인")
    class HasNewNotifications {

        @Test
        @DisplayName("새 알림 있음")
        void 새_알림_있음() {
            // given
            given(notificationRepository.countByUserIdAndIsReadFalse(1L)).willReturn(Optional.of(3L));

            // when
            boolean result = notificationService.hasNewNotifications(1L);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("새 알림 없음")
        void 새_알림_없음() {
            // given
            given(notificationRepository.countByUserIdAndIsReadFalse(1L)).willReturn(Optional.of(0L));

            // when
            boolean result = notificationService.hasNewNotifications(1L);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("createNotification - 알림 생성")
    class CreateNotification {

        @Test
        @DisplayName("FCM 토큰 있고 알림 활성화")
        void FCM_토큰_있고_알림_활성화() {
            // given
            User user = User.builder()
                    .id(1L)
                    .name("테스트")
                    .fcmToken("valid-token")
                    .isReceivedAlarm(true)
                    .build();

            // when
            notificationService.createNotification(user, NotificationType.POINT, "제목", "내용", null);

            // then
            then(notificationRepository).should().save(any(Notification.class));
            then(notificationUtil).should().sendToDevice(eq("valid-token"), eq("제목"), eq("내용"), eq(NotificationType.POINT), isNull());
        }
    }
}
