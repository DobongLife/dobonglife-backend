package com.umust.dobonglife.domain.course.presentation;

import com.umust.dobonglife.domain.course.application.port.in.GetCourseDetailUseCase;
import com.umust.dobonglife.domain.course.application.port.in.GetCourseUseCase;
import com.umust.dobonglife.domain.course.application.port.in.ManageCourseUseCase;
import com.umust.dobonglife.domain.course.application.dto.*;
import com.umust.dobonglife.domain.place.domain.vo.Theme;
import com.umust.dobonglife.global.common.response.CursorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/course")
@RequiredArgsConstructor
public class InternalCourseController {

    private final GetCourseUseCase getCourseUseCase;
    private final ManageCourseUseCase manageCourseUseCase;
    private final GetCourseDetailUseCase getCourseDetailUseCase;

    @PostMapping
    public CourseRegisterResponse createCourse(
            @RequestParam Long userId,
            @RequestBody @Valid CreateCourseRequest request) {
        return manageCourseUseCase.createCourse(userId, request);
    }

    @PatchMapping("/{courseId}")
    public CourseRegisterResponse updateCourse(
            @RequestParam Long userId,
            @PathVariable Long courseId,
            @RequestBody @Valid UpdateCourseRequest request) {
        return manageCourseUseCase.updateCourse(userId, courseId, request);
    }

    @DeleteMapping("/{courseId}")
    public void deleteCourse(@RequestParam Long userId, @PathVariable Long courseId) {
        manageCourseUseCase.deleteCourse(userId, courseId);
    }

    @GetMapping("/my")
    public MyCourseResponse getMyCourses(
            @RequestParam Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam int size) {
        return getCourseUseCase.getMyCourses(userId, lastId, size);
    }

    @GetMapping
    public CursorResponse<CourseSummaryResponse> getAllCourses(
            @RequestParam Long userId,
            @RequestParam(required = false) Theme theme,
            @RequestParam(required = false) Long lastId,
            @RequestParam int size) {
        return getCourseUseCase.getAllCourses(userId, theme, lastId, size);
    }

    @GetMapping("/{courseId}")
    public CourseDetailResponse getCourseDetail(
            @PathVariable Long courseId,
            @RequestParam Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam int size) {
        return getCourseDetailUseCase.getCourseDetail(courseId, userId, lastId, size);
    }
}
