package com.umust.dobonglife.domain.course.service;

import com.umust.dobonglife.domain.course.controller.dto.request.CoursePlanRequest;
import com.umust.dobonglife.domain.course.controller.dto.request.UpdateCourseRequest;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseDeleteResponse;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseRegisterResponse;
import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.entity.CourseDescription;
import com.umust.dobonglife.domain.course.domain.entity.CoursePlans;
import com.umust.dobonglife.domain.course.controller.dto.request.CreateCourseRequest;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.course.domain.vo.CourseBasicInfo;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.global.common.response.CursorUtils;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.external.s3.S3Utils;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.umust.dobonglife.domain.point.domain.vo.PointPolicy.COURSE_CREATE;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final S3Utils s3Utils;
    private final UserService userService;
    private final PointService pointService;


    @Transactional(readOnly = true)
    public CursorResponse<CourseSummaryResponse> getCourses(Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Course> courses = courseRepository.findCoursesNoOffset(lastId, pageable);
        return CursorUtils.toCursorResponse(courses, CourseSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public CursorResponse<CourseSummaryResponse> getCourses(CourseTheme theme, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Course> courses = courseRepository.findByThemeNoOffset(theme, lastId, pageable);
        return CursorUtils.toCursorResponse(courses, CourseSummaryResponse::from);
    }

    public CursorResponse<CourseSummaryResponse> getMyCourses(Long lastId, int size, Long userId) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Course> courses = courseRepository.findMyCoursesNoOffset(userId, lastId, pageable);
        return CursorUtils.toCursorResponse(courses, CourseSummaryResponse::from);
    }

    public CourseRegisterResponse createCourse(Long userId, CreateCourseRequest request, List<MultipartFile> imageFiles) {
        List<String> imageUrls = new ArrayList<>();
        if (imageFiles != null && !imageFiles.isEmpty() && !imageFiles.get(0).isEmpty()) {
            imageUrls = s3Utils.uploadImages(imageFiles);
        }

        try {
            Course savedCourse = createCourseEntity(userId, request, imageUrls);
            return CourseRegisterResponse.from(savedCourse);
        } catch (Exception e) {
            if (!imageUrls.isEmpty()) {
                try {
                    s3Utils.deleteImages(imageUrls);
                } catch (Exception cleanupEx) {
                    log.error("S3 이미지 정리 실패: {}", imageUrls, cleanupEx);
                }
                log.error("코스 등록 실패: {}", request.getTitle());
            }
            throw new BusinessException(ErrorCode.COURSE_SERVER_ERROR);
        }
    }

    @Transactional
    protected Course createCourseEntity(Long userId, CreateCourseRequest request, List<String> imageUrls) {
        CourseBasicInfo basicInfo = CourseBasicInfo.builder()
                .title(request.getTitle())
                .subTitle(request.getSubTitle())
                .duration(request.getDuration())
                .level(CourseLevel.valueOf(request.getLevel()))
                .build();

        CourseDescription description = CourseDescription.builder()
                .content(request.getContent())
                .highlights(request.getHighlights())
                .build();

        List<CoursePlans> plans = convertToPlans(request.getPlans());
        Course course = Course.builder()
                .userId(userId)
                .basicInfo(basicInfo)
                .themes(request.getThemes())
                .tags(request.getTags())
                .imageUrls(imageUrls)
                .description(description)
                .plans(plans)
                .build();

        pointService.earnPoint(userService.findById(userId), COURSE_CREATE.getTitle(), COURSE_CREATE.getPoint());
        userService.updatePoint(userId, COURSE_CREATE.getPoint());

        return courseRepository.save(course);
    }

    public CourseRegisterResponse updateCourse(Long userId, Long courseId, UpdateCourseRequest request, List<MultipartFile> imageFiles) {
        Course course = courseRepository.findByIdWithDescription(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_COURSE_ID));

        boolean isOwner = userService.validateOwner(userId, course.getUserId());
        if(!isOwner) throw new BusinessException(ErrorCode.NOT_OWNER);

        if (imageFiles != null && !imageFiles.isEmpty() && !imageFiles.get(0).isEmpty()) {
            List<String> images = s3Utils.uploadImages(imageFiles);
            course.getImageUrls().addAll(images);
        }
        List<String> urlsToDelete = (request.urlsToDelete() != null) ? new ArrayList<>(request.urlsToDelete()) : new ArrayList<>();
        urlsToDelete.forEach(url -> course.getImageUrls().remove(url));

        try {
            updateCourseEntity(request, course);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    if (!urlsToDelete.isEmpty()) {
                        s3Utils.deleteImages(urlsToDelete);
                    }
                }
            });
            return CourseRegisterResponse.from(course);
        } catch (Exception e) {
            log.error("코스 업데이트 실패: {}", request.title());
            throw new BusinessException(ErrorCode.COURSE_SERVER_ERROR);
        }
    }

    @Transactional
    protected void updateCourseEntity(UpdateCourseRequest request, Course course) {
        course.updateBasicInfo(CourseBasicInfo.builder()
                .title(request.title())
                .subTitle(request.subTitle())
                .duration(request.duration())
                .level(CourseLevel.valueOf(request.level()))
                .build());

        updateCollection(course.getThemes(), request.themes());
        updateCollection(course.getTags(), request.tags());
        course.getDescription().update(request.content(), request.highlights());

        if (request.plans() != null) {
            List<CoursePlans> newPlans = convertToPlans(request.plans());
            course.updatePlans(newPlans);
        }
    }

    private List<CoursePlans> convertToPlans(List<CoursePlanRequest> planDtos) {
        if (planDtos == null) return List.of();
        return planDtos.stream()
                .map(dto -> CoursePlans.builder()
                        .placeId(dto.getPlaceId())
                        .title(dto.getTitle())
                        .content(dto.getContent())
                        .build())
                .toList();
    }

    @Transactional
    public CourseDeleteResponse deleteCourse(Long userId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_COURSE_ID));

        if (!userService.validateOwner(userId, course.getUserId())) {
            throw new BusinessException(ErrorCode.NOT_OWNER);
        }

        List<String> imagesToDelete = new ArrayList<>(course.getImageUrls());
        courseRepository.delete(course);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                if (!imagesToDelete.isEmpty()) {
                    s3Utils.deleteImages(imagesToDelete);
                }
            }
        });
        userService.handleDeletion(userId);
        return CourseDeleteResponse.from(courseId);
    }

    private <T> void updateCollection(List<T> current, List<T> next) {
        current.clear();
        if (next != null) {
            current.addAll(next);
        }
    }

    @Transactional
    public void updateCourseRatingAndCount(Long courseId, Double rating) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Course 엔티티를 찾을 수 없습니다: " + courseId));
        course.applyNewReview(rating);
    }

    @Transactional
    public void deleteCourseReview(Long userId, Long courseId, Double rating) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Course 엔티티를 찾을 수 없습니다: " + courseId));
        course.deleteReview(rating);
    }

    public CursorResponse<CourseSummaryResponse> getLikedCourse(Long userId, int size, Long lastId) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Course> courses = courseRepository.findLikedCourses(userId, null, pageable);
        return CursorUtils.toCursorResponse(courses, CourseSummaryResponse::from);
    }
}
