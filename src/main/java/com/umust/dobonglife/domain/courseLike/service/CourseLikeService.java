package com.umust.dobonglife.domain.courseLike.service;

import com.umust.dobonglife.domain.courseLike.controller.dto.request.CourseLikeResponse;
import com.umust.dobonglife.domain.courseLike.domain.entity.CourseLike;
import com.umust.dobonglife.domain.courseLike.domain.repository.CourseLikeRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseLikeService {
    private final CourseLikeRepository courseLikeRepository;

    public boolean isCourseFavorite(Long userId, Long courseId) {
        return courseLikeRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    @Transactional
    public CourseLikeResponse updateCourseLike(Long courseId, Long userId) {

        return courseLikeRepository.findByCourseIdAndUserId(courseId, userId)
                .map(like -> {
                    courseLikeRepository.delete(like);
                    return CourseLikeResponse.from(userId, courseId, false);
                })
                .orElseGet(() -> {
                    CourseLike newCourseLike = new CourseLike(courseId, userId);
                    courseLikeRepository.save(newCourseLike);
                    return CourseLikeResponse.from(userId, courseId, true);
                });
    }
}
