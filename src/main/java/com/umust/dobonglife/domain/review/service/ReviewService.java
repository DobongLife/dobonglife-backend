package com.umust.dobonglife.domain.review.service;


import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.review.controller.dto.request.ReviewRegisterRequest;
import com.umust.dobonglife.domain.review.domain.entity.Review;
import com.umust.dobonglife.domain.review.domain.constant.Template;
import com.umust.dobonglife.domain.review.domain.repository.ReviewRepository;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final PlaceRepository placeRepository;
    private final CourseRepository courseRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerReview(ReviewRegisterRequest request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Review review = Review.builder()
                .user(user)
                // .reviewImages()
                .title(request.getTitle())
                .rating(request.getRating())
                .templates(request.getTemplates()
                        .stream()
                        .map(Template::toEnum)
                        .toList())
                .build();

        if(request.getPlaceId()!=null) {
            Place place = placeRepository.findById(request.getPlaceId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));
            review.setPlace(place);
        }

        if(request.getCourseId()!=null) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
            review.setCourse(course);
        }

        reviewRepository.save(review);
    }
}
