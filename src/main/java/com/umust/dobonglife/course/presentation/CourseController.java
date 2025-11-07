package com.umust.dobonglife.course.presentation;

import com.umust.dobonglife.course.presentation.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;

    // 홈 메인에서의 스토리 코스 목록 조회
    @GetMapping
    public void getCourses(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "2") int size) {
        Page<CourseSummaryResponse> responses = courseService.getCourses(page, size);
    }
}
