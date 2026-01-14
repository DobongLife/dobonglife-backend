package com.umust.dobonglife.domain.place.service;

import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.domain.courseLike.domain.repository.CourseLikeRepository;
import com.umust.dobonglife.domain.place.controller.dto.request.PlaceRegisterRequest;
import com.umust.dobonglife.domain.place.controller.dto.request.ThemeRequest;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceDetailResponse;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceResponse;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceListResponse;
import com.umust.dobonglife.domain.place.controller.dto.response.*;
import com.umust.dobonglife.domain.place.domain.constant.Amenity;
import com.umust.dobonglife.domain.place.domain.entity.CoursePlace;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.entity.PlaceLike;
import com.umust.dobonglife.domain.place.domain.repository.PlaceLikeRepository;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.review.controller.dto.response.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.service.ReviewService;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;

import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.external.s3.S3Utils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umust.dobonglife.global.common.model.BaseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final PlaceLikeRepository placeLikeRepository;
    private final S3Utils s3Utils;
    private final CourseService courseService;

    @Transactional
    public void registerPlace(PlaceRegisterRequest request, List<MultipartFile> images){

        List<String> imagesUrl = s3Utils.uploadImages(images);

        Place place = Place.builder()
                .name(request.getPlaceName())
                .content(request.getContent())
                .amenities(request.getAmenities().stream()
                        .map(Amenity::toEnum)
                        .toList())
                .address(request.getAddress())
                .contact(request.getContact())
                .operatingHour(request.getOperatingHour())
                .imageUrls(imagesUrl)
                .build();

        placeRepository.save(place);
    }

    @Transactional
    public void toggleLikes(Long userId, Long placeId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));

        Optional<PlaceLike> articleLikesOptional = placeLikeRepository.findByUserIdAndPlaceId(user.getId(), place.getId());

        if(articleLikesOptional.isPresent()) {
            PlaceLike placeLike = articleLikesOptional.get();

            if(placeLike.getStatus() == BaseStatus.INACTIVE) {
                placeLike.restore();
                return;
            }
            else {
                placeLike.softDelete();
                return;
            }
        }

        PlaceLike placeLike = PlaceLike.builder()
                .user(user)
                .place(place)
                .build();
        placeLikeRepository.save(placeLike);
    }

    @Transactional(readOnly = true)
    public PlaceListResponse getLikedPlace(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<Place> likedPlaces = placeLikeRepository.findLikedPlacesByUserId(userId);

        List<PlaceResponse> responses = likedPlaces.stream()
                .map(PlaceResponse::from)
                .toList();

        return PlaceListResponse.from(responses);
    }

    @Transactional(readOnly = true)
    public PlaceAndCourseListResponse getPlaceAndCourseByTheme(ThemeRequest request){
        List<PlaceSummaryResponse> placeResponses = placeRepository.findPlaceSummariesByTheme(CourseTheme.toEnum(request.getTheme()), 3);
        CursorResponse<CourseSummaryResponse> courseResponses = courseService.getCourses(CourseTheme.toEnum(request.getTheme()), 1L, 3);

        return PlaceAndCourseListResponse.from(placeResponses, courseResponses);
    }

    @Transactional(readOnly = true)
    public PlaceSummaryListResponse getPlaceByTheme(ThemeRequest request){
        List<PlaceSummaryResponse> responses = placeRepository.findPlaceSummariesByTheme(CourseTheme.toEnum(request.getTheme()), null);
        return PlaceSummaryListResponse.from(responses);
    }
    @Transactional
    public void updatePlaceRatingAndCount(Long placeId, Double rating) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Place 엔티티를 찾을 수 없습니다: " + placeId));
        place.applyNewReview(rating);
        placeRepository.save(place);
    }

    @Transactional
    public PlaceSummaryListResponse getAllPlace(Long userId) {
        List<PlaceSummaryResponse> responses = placeRepository.findPlaceSummaries(userId);
        return PlaceSummaryListResponse.from(responses);
    }
}
