package com.umust.dobonglife.domain.like.presentation;

import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.port.content.LikePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikePort likePort;

    @PostMapping("/place/{placeId}")
    public ResponseEntity<BaseResponse<Boolean>> likePlace(@CurrentUserId Long userId,
                                                           @PathVariable Long placeId) {
        boolean liked = likePort.toggleLike(userId, TargetType.PLACE, placeId);
        return ResponseEntity.ok(BaseResponse.ok(liked));
    }

    @GetMapping("/place/my")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getMyLikedPlaces(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.PLACE) int size) {
        return ResponseEntity.ok(BaseResponse.ok(likePort.getMyLikedPlaces(userId, lastId, size)));
    }

    @PostMapping("/course/{courseId}")
    public ResponseEntity<BaseResponse<Boolean>> likeCourse(@CurrentUserId Long userId,
                                                            @PathVariable Long courseId) {
        boolean liked = likePort.toggleLike(userId, TargetType.COURSE, courseId);
        return ResponseEntity.ok(BaseResponse.ok(liked));
    }

    @GetMapping("/course/my")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getMyLikedCourses(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.COURSE) int size) {
        return ResponseEntity.ok(BaseResponse.ok(likePort.getMyLikedCourses(userId, lastId, size)));
    }
}
