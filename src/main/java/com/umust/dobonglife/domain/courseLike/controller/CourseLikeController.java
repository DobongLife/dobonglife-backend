package com.umust.dobonglife.domain.courseLike.controller;

import com.umust.dobonglife.domain.courseLike.controller.dto.request.CourseLikeResponse;
import com.umust.dobonglife.domain.courseLike.service.CourseLikeService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "코스 API", description = "코스 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course/like")
public class CourseLikeController {

    private final CourseLikeService courseLikeService;

    @Operation(summary = "코스 찜하기", description = "코스를 찜합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @PostMapping("/{courseId}")
    public BaseResponse<CourseLikeResponse> updateCourseLike(
            @PathVariable("courseId") Long courseId,
            @CurrentUserId Long userId) {
        CourseLikeResponse responses = courseLikeService.updateCourseLike(courseId, userId);
        return BaseResponse.ok(responses);
    }
}