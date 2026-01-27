package com.umust.dobonglife.domain.notification.presentation;

import com.umust.dobonglife.domain.notification.presentation.dto.response.NotificationResponse;
import com.umust.dobonglife.domain.notification.service.NotificationService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
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

    // 알림목록 조회 및 필터링
    @GetMapping
    public BaseResponse<CursorResponse<NotificationResponse>> getNotifications(
            @CurrentUserId Long userId,
            @RequestParam(defaultValue = "ALL") String filter,
            @RequestParam(required = false, defaultValue = "5") Long lastId,
            @RequestParam(defaultValue = "2") int size) {
        CursorResponse<NotificationResponse> response = notificationService.getNotifications(userId, filter, lastId, size);
        return BaseResponse.ok(response);
    }

    // 새로운 알림 유무 확인
    @GetMapping("/new")
    public ResponseEntity<Boolean> checkNewNotifications(@CurrentUserId Long userId) {
        boolean hasNew = notificationService.hasNewNotifications(userId);
        return ResponseEntity.ok(hasNew);
    }

    // 특정 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@CurrentUserId Long userId,
                                           @PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.noContent().build();
    }
}
