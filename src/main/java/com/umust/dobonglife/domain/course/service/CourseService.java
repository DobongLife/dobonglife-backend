package com.umust.dobonglife.domain.course.service;

import com.umust.dobonglife.domain.course.controller.dto.response.CourseDetailResponse;
import com.umust.dobonglife.domain.course.controller.dto.request.CoursePlanRequest;
import com.umust.dobonglife.domain.course.controller.dto.request.UpdateCourseRequest;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseDeleteResponse;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseRegisterResponse;
import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.entity.CourseDescription;
import com.umust.dobonglife.domain.course.domain.entity.CoursePlans;
import com.umust.dobonglife.domain.course.domain.repository.CoursePlansRepository;
import com.umust.dobonglife.domain.course.controller.dto.request.CreateCourseRequest;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.course.domain.vo.CourseBasicInfo;
import com.umust.dobonglife.domain.course.domain.vo.CourseOperationInfo;
import com.umust.dobonglife.domain.course.domain.vo.CoursePolicyInfo;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.ErrorCode;
import com.umust.dobonglife.global.common.s3.S3Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CoursePlansRepository coursePlansRepository;
    private final S3Utils s3Utils;
    private final UserService userService;

    @Transactional(readOnly = true)
    public CursorResponse<CourseSummaryResponse> getCourses(Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Course> courses = courseRepository.findCoursesNoOffset(lastId, pageable);
        return convertToCursorResponse(courses);
    }

    // TODO: 종윤님께서 주간테마 코스 조회 시 사용할 함수
    @Transactional(readOnly = true)
    public CursorResponse<CourseSummaryResponse> getCourses(CourseTheme theme, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Course> courses = courseRepository.findByThemeNoOffset(theme, lastId, pageable);
        return convertToCursorResponse(courses);
    }

    @Transactional(readOnly = true)
    public CourseDetailResponse getCourse(Long courseId) {
        Course course = courseRepository.findByIdWithDescription(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_COURSE_ID));

        List<CoursePlans> plans = coursePlansRepository
                .findByCourseIdOrderByDateTime(courseId);

        return CourseDetailResponse.from(course, plans);
    }

    @Transactional
    public CourseRegisterResponse createCourse(Long userId, CreateCourseRequest request, List<MultipartFile> imageFiles) {
        // 이미지
        List<String> imageUrls = new ArrayList<>();
        if(imageFiles != null && !imageFiles.isEmpty() && !imageFiles.get(0).isEmpty()) {
            imageUrls = s3Utils.uploadImages(imageFiles);
        }

        // vo
        CourseBasicInfo basicInfo = new CourseBasicInfo(request.title(), request.subTitle(), request.duration(), CourseLevel.valueOf(request.level()));
        CourseOperationInfo operationInfo = new CourseOperationInfo(request.meetingPlace(), request.contact(), request.cost(), request.maxNum(), request.ageLimit());
        CoursePolicyInfo policyInfo = new CoursePolicyInfo(request.cancelPolicy(), request.weatherPolicy());

        // 상세 설명
        CourseDescription description = new CourseDescription(
                null, request.content(), request.highlights(), request.exclusions(), request.inclusions()
        );

        List<CoursePlans> plans = convertToPlans(request.plans());

        Course course = Course.builder()
                .userId(userId)
                .basicInfo(basicInfo)
                .operationInfo(operationInfo)
                .policyInfo(policyInfo)
                .themes(request.themes())
                .tags(request.tags())
                .imageUrls(imageUrls)
                .description(description)
                .plans(plans)
                .build();

        Course savedCourse = courseRepository.save(course);

        return CourseRegisterResponse.from(savedCourse);
    }

    @Transactional
    public CourseRegisterResponse updateCourse(Long courseId, UpdateCourseRequest request, List<MultipartFile> imageFiles) {
        Course course = courseRepository.findByIdWithDescription(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_COURSE_ID));

        updateImageUrls(course, request.urlsToDelete(), imageFiles);

        course.updateBasicInfo(new CourseBasicInfo(request.title(), request.subTitle(), request.duration(), CourseLevel.valueOf(request.level())));
        course.updateOperationInfo(new CourseOperationInfo(request.meetingPlace(), request.contact(), request.cost(), request.maxNum(), request.ageLimit()));
        course.updatePolicyInfo(new CoursePolicyInfo(request.cancelPolicy(), request.weatherPolicy()));

        updateCollection(course.getThemes(), request.themes());
        updateCollection(course.getTags(), request.tags());

        course.getDescription().update(
                request.content(), request.highlights(), request.exclusions(), request.inclusions()
        );

        if (request.plans() != null) {
            List<CoursePlans> newPlans = convertToPlans(request.plans());
            course.updatePlans(newPlans);
        }

        return CourseRegisterResponse.from(course);
    }

    private List<CoursePlans> convertToPlans(List<CoursePlanRequest> planDtos) {
        if (planDtos == null) return List.of();
        return planDtos.stream()
                .map(dto -> CoursePlans.builder()
                        .dateTime(dto.dateTime())
                        .title(dto.title())
                        .content(dto.content())
                        .build())
                .toList();
    }

    private void updateImageUrls(Course course, List<String> urlsToDelete, List<MultipartFile> newFiles) {
        if (urlsToDelete != null && !urlsToDelete.isEmpty() && !urlsToDelete.get(0).isEmpty()) {
            urlsToDelete.forEach(url -> {
                s3Utils.deleteImage(url);
                course.getImageUrls().remove(url);
            });
        }

        if (newFiles != null && !newFiles.isEmpty() && !newFiles.get(0).isEmpty()) {
            List<String> images = s3Utils.uploadImages(newFiles);
            course.getImageUrls().addAll(images);
        }
    }

    private <T> void updateCollection(List<T> current, List<T> next) {
        current.clear();
        if (next != null) {
            current.addAll(next);
        }
    }

    // TODO: 삭제할때 특정조건 고려
    @Transactional
    public CourseDeleteResponse deleteCourse(Long userId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_COURSE_ID));

        boolean isRemoved = userService.isCourseRemoved(userId, course.getUserId());
        if(!isRemoved)
            throw new BusinessException(ErrorCode.NOT_COURSE_OWNER);

        if (course.getImageUrls() != null && !course.getImageUrls().isEmpty()) {
            s3Utils.deleteImages(course.getImageUrls());
        }
        courseRepository.delete(course);
        return CourseDeleteResponse.from(courseId);
    }

    private CursorResponse<CourseSummaryResponse> convertToCursorResponse(Slice<Course> courses) {
        List<CourseSummaryResponse> content = courses.getContent().stream()
                .map(CourseSummaryResponse::from)
                .toList();
        return new CursorResponse<>(content, courses.hasNext());
    }
}
