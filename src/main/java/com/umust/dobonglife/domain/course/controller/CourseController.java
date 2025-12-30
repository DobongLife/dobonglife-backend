package com.umust.dobonglife.domain.course.controller;

import com.umust.dobonglife.domain.auth.domain.entity.UserPrincipal;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseDeleteResponse;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseDetailResponse;
import com.umust.dobonglife.domain.course.controller.dto.request.CreateCourseRequest;
import com.umust.dobonglife.domain.course.controller.dto.request.UpdateCourseRequest;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseRegisterResponse;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.global.common.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.CursorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "코스 API", description = "코스 관련 API")
@SecurityRequirement(name = "BearerAuth")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseController {

    private final CourseService courseService;

    // 홈 메인에서의 스토리 코스 목록 조회
    @Operation(summary = "코스 목록 조회 (홈메인)", description = "코스를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping
    public BaseResponse<CursorResponse<CourseSummaryResponse>> getCourses(@RequestParam(required = false) Long lastId,
                                                                          @RequestParam(defaultValue = "2") int size) {
        CursorResponse<CourseSummaryResponse> responses = courseService.getCourses(lastId, size);
        return BaseResponse.ok(responses);
    }

    // 코스 상세보기 조회
    @Operation(summary = "코스 상세보기 조회", description = "코스를 상세 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping("/{courseId}")
    public BaseResponse<CourseDetailResponse> getCourse(@CurrentUserId Long userId, @PathVariable("courseId") Long courseId){
        CourseDetailResponse response = courseService.getCourse(userId, courseId);
        return BaseResponse.ok(response);
    }

    // 코스 등록하기
    @Operation(summary = "코스 등록", description = "코스를 등록합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @PostMapping
    public BaseResponse<CourseRegisterResponse> createCourse(
            @CurrentUserId Long userId,
            @RequestPart("request") @Valid CreateCourseRequest request, @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles){
        CourseRegisterResponse response = courseService.createCourse(userId, request, imageFiles);
        return BaseResponse.ok(response);
    }

    // 코스 수정하기
    @Operation(summary = "코스 수정", description = "코스를 수정합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @PutMapping("/{courseId}")
    public BaseResponse<CourseRegisterResponse> updateCourse(
            @PathVariable("courseId") Long courseId,
            @RequestPart("request") @Valid UpdateCourseRequest request, @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles){
        CourseRegisterResponse response = courseService.updateCourse(courseId, request,imageFiles);
        return BaseResponse.ok(response);
    }

    @Operation(summary = "코스 삭제", description = "코스를 삭제합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @DeleteMapping("/{courseId}")
    public BaseResponse<CourseDeleteResponse> deleteCourse(
            @CurrentUserId Long userId,
            @PathVariable("courseId") Long courseId){
        CourseDeleteResponse response = courseService.deleteCourse(userId, courseId);
        return BaseResponse.ok(response);
    }
}
