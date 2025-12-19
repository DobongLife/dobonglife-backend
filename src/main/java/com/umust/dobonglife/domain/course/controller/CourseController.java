package com.umust.dobonglife.domain.course.controller;

import com.umust.dobonglife.domain.auth.domain.entity.UserPrincipal;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseDeleteResponse;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseDetailResponse;
import com.umust.dobonglife.domain.course.controller.dto.request.CreateCourseRequest;
import com.umust.dobonglife.domain.course.controller.dto.request.UpdateCourseRequest;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseRegisterResponse;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;

    // 홈 메인에서의 스토리 코스 목록 조회
    @GetMapping
    public BaseResponse<CursorResponse<CourseSummaryResponse>> getCourses(@RequestParam(required = false) Long lastCourseId,
                                                                          @RequestParam(defaultValue = "2") int size) {
        CursorResponse<CourseSummaryResponse> responses = courseService.getCourses(lastCourseId, size);
        return BaseResponse.ok(responses);
    }

    // 코스 상세보기 조회
    @GetMapping("/{courseId}")
    public BaseResponse<CourseDetailResponse> getCourse(@PathVariable("courseId") Long courseId){
        CourseDetailResponse response = courseService.getCourse(courseId);
        return BaseResponse.ok(response);
    }

    // 코스 등록하기
    @PostMapping
    public BaseResponse<CourseRegisterResponse> createCourse(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestPart("request") @Valid CreateCourseRequest request, @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles){
        Long userId = userPrincipal.getUserId();
        CourseRegisterResponse response = courseService.createCourse(userId, request, imageFiles);
        return BaseResponse.ok(response);
    }

    // 코스 수정하기
    @PostMapping("/{courseId}")
    public BaseResponse<CourseRegisterResponse> updateCourse(
            @PathVariable("courseId") Long courseId,
            @RequestPart("request") @Valid UpdateCourseRequest request, @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles){
        CourseRegisterResponse response = courseService.updateCourse(courseId, request,imageFiles);
        return BaseResponse.ok(response);
    }

    @DeleteMapping("/{courseId}")
    public BaseResponse<CourseDeleteResponse> deleteCourse(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("courseId") Long courseId){
        Long userId = userPrincipal.getUserId();
        CourseDeleteResponse response = courseService.deleteCourse(userId, courseId);
        return BaseResponse.ok(response);
    }
}
