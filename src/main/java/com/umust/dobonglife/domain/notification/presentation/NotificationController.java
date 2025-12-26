package com.umust.dobonglife.domain.notification.presentation;

import com.umust.dobonglife.domain.notification.presentation.dto.response.NotificationResponse;
import com.umust.dobonglife.domain.notification.service.NotificationService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    //TODO: 로그인 설정 이후 추출로 변경
    private static final Long TEMP_USER_ID = 1L;

    // 알림목록 조회 및 필터링
    @GetMapping
    public BaseResponse<Page<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "ALL") String filter,
            Pageable pageable) {
        Page<NotificationResponse> notifications = notificationService.getNotifications(TEMP_USER_ID, filter, pageable);
        return BaseResponse.ok(notifications);
    }

    // 새로운 알림 유무 확인
    @GetMapping("/new")
    public ResponseEntity<Boolean> checkNewNotifications() {
        boolean hasNew = notificationService.hasNewNotifications(TEMP_USER_ID);
        return ResponseEntity.ok(hasNew);
    }

    // 특정 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId, TEMP_USER_ID);
        return ResponseEntity.noContent().build();
    }
}
