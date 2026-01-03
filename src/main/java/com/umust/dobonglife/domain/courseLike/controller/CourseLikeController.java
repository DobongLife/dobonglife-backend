package com.umust.dobonglife.domain.courseLike.controller;

import com.umust.dobonglife.domain.courseLike.controller.dto.request.CourseLikeResponse;
import com.umust.dobonglife.domain.courseLike.service.CourseLikeService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course/like")
public class CourseLikeController {

    private final CourseLikeService courseLikeService;

    @PostMapping("/{courseId}")
    public BaseResponse<CourseLikeResponse> updateCourseLike(
            @PathVariable("courseId") Long courseId,
            @CurrentUserId Long userId) {
        CourseLikeResponse responses = courseLikeService.updateCourseLike(courseId, userId);
        return BaseResponse.ok(responses);
    }
}