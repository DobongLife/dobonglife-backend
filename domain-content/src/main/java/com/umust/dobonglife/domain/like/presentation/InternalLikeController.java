package com.umust.dobonglife.domain.like.presentation;

import com.umust.dobonglife.domain.like.application.LikeService;
import com.umust.dobonglife.domain.like.application.dto.MyLikedCourseResponse;
import com.umust.dobonglife.domain.like.application.dto.MyLikedPlaceResponse;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/like")
@RequiredArgsConstructor
public class InternalLikeController {

    private final LikeService likeService;

    @PostMapping("/{targetType}/{targetId}")
    public boolean toggleLike(
            @RequestParam Long userId,
            @PathVariable TargetType targetType,
            @PathVariable Long targetId) {
        return likeService.toggleLike(userId, targetType, targetId);
    }

    @GetMapping("/place/my")
    public CursorResponse<MyLikedPlaceResponse> getMyLikedPlaces(
            @RequestParam Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam int size) {
        return likeService.getMyLikedPlaces(userId, lastId, size);
    }

    @GetMapping("/course/my")
    public CursorResponse<MyLikedCourseResponse> getMyLikedCourses(
            @RequestParam Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam int size) {
        return likeService.getMyLikedCourses(userId, lastId, size);
    }
}
