package com.umust.dobonglife.course.presentation;

import com.umust.dobonglife.course.presentation.dto.response.CourseDetailResponse;
import com.umust.dobonglife.course.presentation.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.course.service.CourseService;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;

    // 홈 메인에서의 스토리 코스 목록 조회
    @GetMapping
    public BaseResponse<Page<CourseSummaryResponse>> getCourses(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "2") int size) {
        Page<CourseSummaryResponse> responses = courseService.getCourses(page, size);
        return BaseResponse.ok(responses);
    }

    // 코스 상세보기 조회
    @GetMapping("/{courseId}")
    public BaseResponse<CourseDetailResponse> getCourse(@PathVariable("courseId") Integer courseId){
        CourseDetailResponse response = courseService.getCourse(courseId);
        return BaseResponse.ok(response);
    }
}
