package com.umust.dobonglife.domain.courseLike.service;

import com.umust.dobonglife.domain.courseLike.domain.repository.CourseLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseLikeService {
    private final CourseLikeRepository courseLikeRepository;

    public boolean isCourseFavorite(Long userId, Long courseId) {
        return courseLikeRepository.existsByUserIdAndCourseId(userId, courseId);
    }
}
