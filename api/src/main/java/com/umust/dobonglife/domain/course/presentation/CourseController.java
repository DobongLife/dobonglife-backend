package com.umust.dobonglife.domain.course.presentation;

import com.umust.dobonglife.domain.course.application.CourseDetailService;
import com.umust.dobonglife.domain.course.application.dto.CourseDetailResponse;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseDetailService courseDetailService;

    @GetMapping("/{courseId}")
    public BaseResponse<CourseDetailResponse> getCourseDetail(
            @PathVariable Long courseId,
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "2") int size) {
        return BaseResponse.ok(courseDetailService.getCourseDetail(courseId, userId, lastId, size));
    }
}
