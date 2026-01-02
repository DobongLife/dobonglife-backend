package com.umust.dobonglife.domain.course.presentation;

import com.umust.dobonglife.domain.course.presentation.dto.request.CreateCourseRequest;
import com.umust.dobonglife.domain.course.presentation.dto.response.CourseDetailResponse;
import com.umust.dobonglife.domain.course.presentation.dto.response.CourseResponse;
import com.umust.dobonglife.domain.course.presentation.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceListResponse;
import com.umust.dobonglife.global.common.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
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

    // 코스 등록하기
    @PostMapping
    public BaseResponse<CourseResponse> registerCourse(@RequestBody @Valid CreateCourseRequest request){
        CourseResponse response = courseService.registerCourse(request);
        return BaseResponse.ok(response);
    }

    @Operation(summary = "주간 테마별 코스 조회", description = "주간 테마별 코스를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "주간 테마별 코스 조회에 성공하였습니다."
    )
    @GetMapping("/theme")
    public BaseResponse<PlaceListResponse> getCourseByTheme(@CurrentUserId Long userId){
        return BaseResponse.ok(courseService.getLikedPlace(userId));
    }
}
