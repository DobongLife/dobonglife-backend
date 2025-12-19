package com.umust.dobonglife.domain.course.controller;

import com.umust.dobonglife.domain.course.controller.dto.CourseDetailResponse;
import com.umust.dobonglife.domain.course.controller.dto.request.CreateCourseRequest;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public BaseResponse<CourseResponse> registerCourse(@RequestBody @Valid CreateCourseRequest request){
        CourseResponse response = courseService.registerCourse(request);
        return BaseResponse.ok(response);
    }
}
