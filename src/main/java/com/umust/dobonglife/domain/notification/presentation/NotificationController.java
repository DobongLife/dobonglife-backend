package com.umust.dobonglife.domain.notification.presentation;

import com.umust.dobonglife.domain.notification.presentation.dto.response.NotificationResponse;
import com.umust.dobonglife.domain.notification.service.NotificationService;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "알림 API", description = "알림 관련 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @Operation(summary = "알림 목록 조회", description = "알림목록을 필터에 따라 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    // 알림목록 조회 및 필터링
    @GetMapping
    public BaseResponse<CursorResponse<NotificationResponse>> getNotifications(
            @CurrentUserId Long userId,
            @RequestParam(defaultValue = "ALL") String filter,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "2") int size) {
        CursorResponse<NotificationResponse> response = notificationService.getNotifications(userId, filter, lastId, size);
        return BaseResponse.ok(response);
    }

    @Operation(summary = "새로운 알림 유무 확인", description = "새로운 알림이 있으면 true가 반환됩니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    // 새로운 알림 유무 확인
    @GetMapping("/new")
    public ResponseEntity<Boolean> checkNewNotifications(@CurrentUserId Long userId) {
        boolean hasNew = notificationService.hasNewNotifications(userId);
        return ResponseEntity.ok(hasNew);
    }

    @Operation(summary = "특정 알림 확인", description = "특정 알림을 읽음처리합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    // 특정 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@CurrentUserId Long userId,
                                           @PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "알림 설정 유무", description = "푸시 알림 설정을 제어합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @PatchMapping("/settings/notification")
    public ResponseEntity<Void> toggleNotification(@CurrentUserId Long userId, @RequestParam boolean enabled) {
        userService.updateNotificationSetting(userId, enabled);
        return ResponseEntity.ok().build();
    }
}
