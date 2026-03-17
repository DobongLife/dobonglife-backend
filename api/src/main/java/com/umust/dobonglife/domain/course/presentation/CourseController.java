package com.umust.dobonglife.domain.course.presentation;

import com.umust.dobonglife.domain.course.application.CourseDetailService;
import com.umust.dobonglife.domain.course.application.CourseService;
import com.umust.dobonglife.domain.course.application.dto.CourseDetailResponse;
import com.umust.dobonglife.domain.course.application.dto.CourseRegisterResponse;
import com.umust.dobonglife.domain.course.application.dto.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.application.dto.CreateCourseRequest;
import com.umust.dobonglife.domain.course.application.dto.MyCourseResponse;
import com.umust.dobonglife.domain.course.application.dto.UpdateCourseRequest;
import com.umust.dobonglife.domain.place.domain.vo.Theme;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final CourseDetailService courseDetailService;

    @PostMapping
    public BaseResponse<CourseRegisterResponse> createCourse(
            @CurrentUserId Long userId,
            @RequestBody @Valid CreateCourseRequest request) {
        return BaseResponse.ok(courseService.createCourse(userId, request));
    }

    @PatchMapping("/{courseId}")
    public BaseResponse<CourseRegisterResponse> updateCourse(
            @CurrentUserId Long userId,
            @PathVariable Long courseId,
            @RequestBody @Valid UpdateCourseRequest request) {
        return BaseResponse.ok(courseService.updateCourse(userId, courseId, request));
    }

    @GetMapping("/my")
    public BaseResponse<MyCourseResponse> getMyCourses(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.COURSE) int size) {
        return BaseResponse.ok(courseService.getMyCourses(userId, lastId, size));
    }

    @GetMapping
    public BaseResponse<CursorResponse<CourseSummaryResponse>> getAllCourses(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Theme theme,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.COURSE) int size) {
        return BaseResponse.ok(courseService.getAllCourses(userId, theme, lastId, size));
    }

    @GetMapping("/{courseId}")
    public BaseResponse<CourseDetailResponse> getCourseDetail(
            @PathVariable Long courseId,
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "2") int size) {
        return BaseResponse.ok(courseDetailService.getCourseDetail(courseId, userId, lastId, size));
    }
}
