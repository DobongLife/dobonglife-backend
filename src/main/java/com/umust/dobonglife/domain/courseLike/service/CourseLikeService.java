package com.umust.dobonglife.domain.courseLike.service;

import com.umust.dobonglife.domain.courseLike.controller.dto.request.CourseLikeResponse;
import com.umust.dobonglife.domain.courseLike.domain.entity.CourseLike;
import com.umust.dobonglife.domain.courseLike.domain.repository.CourseLikeRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseLikeService {
    private final CourseLikeRepository courseLikeRepository;

    public boolean isCourseFavorite(Long userId, Long courseId) {
        return courseLikeRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    @Transactional
    public CourseLikeResponse updateCourseLike(Long courseId, Long userId) {
        CourseLike courseLike = courseLikeRepository.findByCourseIdAndUserId(courseId, userId)
                .orElseThrow(() -> new EntityExistsException("코스 좋아요 엔티티를 찾을 수 없습니다."));

        if(courseLike != null){
            courseLikeRepository.delete(courseLike);
            return CourseLikeResponse.from(userId, courseId, false);
        }

        CourseLike newCourseLike = new CourseLike(courseId, userId);
        courseLikeRepository.save(newCourseLike);
        return CourseLikeResponse.from(userId, courseId, true);
    }
}
