package com.umust.dobonglife.domain.course.presentation;

import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.port.content.CoursePort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CoursePort coursePort;

    @PostMapping
    public BaseResponse<Map<String, Object>> createCourse(
            @CurrentUserId Long userId,
            @RequestBody Map<String, Object> request) {
        return BaseResponse.ok(coursePort.createCourse(userId, request));
    }

    @PatchMapping("/{courseId}")
    public BaseResponse<Map<String, Object>> updateCourse(
            @CurrentUserId Long userId,
            @PathVariable Long courseId,
            @RequestBody Map<String, Object> request) {
        return BaseResponse.ok(coursePort.updateCourse(userId, courseId, request));
    }

    @GetMapping("/my")
    public BaseResponse<Map<String, Object>> getMyCourses(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.COURSE) int size) {
        return BaseResponse.ok(coursePort.getMyCourses(userId, lastId, size));
    }

    @GetMapping
    public BaseResponse<Map<String, Object>> getAllCourses(
            @CurrentUserId Long userId,
            @RequestParam(required = false) String theme,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.COURSE) int size) {
        return BaseResponse.ok(coursePort.getAllCourses(userId, theme, lastId, size));
    }

    @DeleteMapping("/{courseId}")
    public BaseResponse<Void> deleteCourse(
            @CurrentUserId Long userId,
            @PathVariable Long courseId) {
        coursePort.deleteCourse(userId, courseId);
        return BaseResponse.ok(null);
    }

    @GetMapping("/{courseId}")
    public BaseResponse<Map<String, Object>> getCourseDetail(
            @PathVariable Long courseId,
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.REVIEW_PREVIEW) int size) {
        return BaseResponse.ok(coursePort.getCourseDetail(courseId, userId, lastId, size));
    }
}
