package com.umust.dobonglife.content.controller;

import com.umust.dobonglife.domain.like.application.LikeService;
import com.umust.dobonglife.domain.like.application.dto.MyLikedCourseResponse;
import com.umust.dobonglife.domain.like.application.dto.MyLikedPlaceResponse;
import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/place/{placeId}")
    public ResponseEntity<BaseResponse<Boolean>> likePlace(
            @CurrentUserId Long userId, @PathVariable Long placeId) {
        return ResponseEntity.ok(BaseResponse.ok(likeService.toggleLike(userId, TargetType.PLACE, placeId)));
    }

    @GetMapping("/place/my")
    public ResponseEntity<BaseResponse<CursorResponse<MyLikedPlaceResponse>>> getMyLikedPlaces(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.PLACE) int size) {
        return ResponseEntity.ok(BaseResponse.ok(likeService.getMyLikedPlaces(userId, lastId, size)));
    }

    @PostMapping("/course/{courseId}")
    public ResponseEntity<BaseResponse<Boolean>> likeCourse(
            @CurrentUserId Long userId, @PathVariable Long courseId) {
        return ResponseEntity.ok(BaseResponse.ok(likeService.toggleLike(userId, TargetType.COURSE, courseId)));
    }

    @GetMapping("/course/my")
    public ResponseEntity<BaseResponse<CursorResponse<MyLikedCourseResponse>>> getMyLikedCourses(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.COURSE) int size) {
        return ResponseEntity.ok(BaseResponse.ok(likeService.getMyLikedCourses(userId, lastId, size)));
    }
}
