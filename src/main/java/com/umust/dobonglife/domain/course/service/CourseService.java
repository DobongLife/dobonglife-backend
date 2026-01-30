package com.umust.dobonglife.domain.course.service;

import com.umust.dobonglife.domain.course.controller.dto.request.CoursePlanRequest;
import com.umust.dobonglife.domain.course.controller.dto.request.UpdateCourseRequest;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseDeleteResponse;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseMyResponse;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseRegisterResponse;
import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.entity.CourseDescription;
import com.umust.dobonglife.domain.course.domain.entity.CoursePlans;
import com.umust.dobonglife.domain.course.controller.dto.request.CreateCourseRequest;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.domain.repository.CoursePlansRepository;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.course.domain.vo.CourseBasicInfo;
import com.umust.dobonglife.domain.point.service.PointService;
import com.umust.dobonglife.domain.courseLike.service.CourseLikeService;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.umust.dobonglife.domain.point.domain.vo.PointPolicy.COURSE_CREATE;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CoursePlansRepository coursePlansRepository;
    private final S3Utils s3Utils;
    private final UserService userService;
    private final PointService pointService;
    private final CourseLikeService courseLikeService;

    @Transactional(readOnly = true)
    public CursorResponse<CourseSummaryResponse> getCourses(Long userId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Course> courses = courseRepository.findCoursesNoOffset(lastId, pageable);

        return getCourseSummaryResponseCursorResponse(userId, courses);
    }

    @Transactional(readOnly = true)
    public CursorResponse<CourseSummaryResponse> getCourses(Long userId, CourseTheme theme, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Course> courses = courseRepository.findByThemeNoOffset(theme, lastId, pageable);

        return getCourseSummaryResponseCursorResponse(userId, courses);
    }

    private CursorResponse<CourseSummaryResponse> getCourseSummaryResponseCursorResponse(Long userId, Slice<Course> courses) {
        List<Long> courseIds = courses.getContent().stream()
                .map(Course::getId)
                .toList();

        Set<Long> favoriteCourseIds = (userId != null)
                ? courseLikeService.getFavoriteCourseIds(userId, courseIds)
                : Collections.emptySet();

        return CursorUtils.toCursorResponse(courses, course ->
                CourseSummaryResponse.of(course, favoriteCourseIds.contains(course.getId()))
        );
    }

    public CourseMyResponse getMyCourses(Long lastId, int size, Long userId) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Course> courses = courseRepository.findMyCoursesNoOffset(userId, lastId, pageable);

        Long totalCount = courseRepository.countByUserId(userId);
        CursorResponse<CourseSummaryResponse> course = getCourseSummaryResponseCursorResponse(userId, courses);
        return CourseMyResponse.from(totalCount, course);
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
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.COURSE_SERVER_ERROR);
        }
    }

    @Transactional
    protected Course createCourseEntity(Long userId, CreateCourseRequest request, List<String> imageUrls) {
        log.info("Request DTO: {}", request);

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
        Course course = Course.builder()
                .userId(userId)
                .basicInfo(basicInfo)
                .themes(request.getThemes())
                .tags(request.getTags())
                .imageUrls(imageUrls)
                .description(description)
                .build();

        pointService.earnPoint(userService.findById(userId), COURSE_CREATE.getTitle(), COURSE_CREATE.getPoint());
        userService.updatePoint(userId, COURSE_CREATE.getPoint());

        Course save = courseRepository.save(course);
        List<CoursePlans> plans = convertToPlans(course, request.getPlans());
        coursePlansRepository.saveAll(plans);

        return save;
    }

    @Transactional
    public CourseRegisterResponse updateCourse(Long userId, Long courseId, UpdateCourseRequest request, List<MultipartFile> imageFiles) {
        Course course = courseRepository.findByIdWithDescription(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_COURSE_ID));

        boolean isOwner = userService.validateOwner(userId, course.getUserId());
        if(!isOwner) throw new BusinessException(ErrorCode.NOT_OWNER);

        List<String> urlsToDelete = (request.urlsToDelete() != null) ? new ArrayList<>(request.urlsToDelete()) : new ArrayList<>();
        urlsToDelete.forEach(url -> course.getImageUrls().remove(url));

        if (imageFiles != null && !imageFiles.isEmpty() && !imageFiles.get(0).isEmpty()) {
            List<String> images = s3Utils.uploadImages(imageFiles);
            course.getImageUrls().addAll(images);
        }

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
            log.error("코스 업데이트 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.COURSE_SERVER_ERROR);
        }
    }

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
            coursePlansRepository.deleteByCourseCustom(course);
            List<CoursePlans> coursePlans = convertToPlans(course, request.plans());
            coursePlansRepository.saveAll(coursePlans);
        }
    }

    private List<CoursePlans> convertToPlans(Course course, List<CoursePlanRequest> planDtos) {
        if (planDtos == null) return List.of();
        return planDtos.stream()
                .map(dto -> {
                    log.info("[convertToPlans] DTO placeId: {}, Order: {}, Title: {}",
                            dto.getPlaceId(), dto.getOrder(), dto.getTitle());

                    CoursePlans plan = CoursePlans.builder()
                            .course(course)
                            .placeId(dto.getPlaceId())
                            .title(dto.getTitle())
                            .isOrder(dto.getOrder())
                            .content(dto.getContent())
                            .build();

                    log.info("[convertToPlans] Entity placeId after mapping: {}", plan.getPlaceId());

                    return plan;
                })
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

        return getCourseSummaryResponseCursorResponse(userId, courses);
    }
}
