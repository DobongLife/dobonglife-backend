package com.umust.dobonglife.domain.place.service;


import com.umust.dobonglife.domain.course.controller.dto.response.CourseResponse;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.course.domain.repository.CourseRepository;
import com.umust.dobonglife.domain.place.controller.dto.request.PlaceRegisterRequest;
import com.umust.dobonglife.domain.place.controller.dto.request.ThemeRequest;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceResponse;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceListResponse;
import com.umust.dobonglife.domain.place.domain.constant.Amenity;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.entity.PlaceLike;
import com.umust.dobonglife.domain.place.domain.repository.PlaceLikeRepository;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.ErrorCode;
import com.umust.dobonglife.global.common.s3.S3Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umust.dobonglife.global.common.model.BaseStatus;
import org.springframework.web.multipart.MultipartFile;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceAndCourseListResponse;

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
    private final CourseRepository courseRepository;

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
        List<PlaceResponse> placeResponses = placeRepository.fetchPlaceResponsesByTheme(CourseTheme.toEnum(request.getTheme()), 3);

        List<Course> courses = courseRepository.findDistinctPlacesByThemeLimit3(CourseTheme.toEnum(request.getTheme()));

        List<CourseResponse> courseResponses = courseRepository.findCourseResponsesByTheme(CourseTheme.toEnum(request.getTheme()));

        return PlaceAndCourseListResponse.from(placeResponses, courseResponses);
    }

    @Transactional(readOnly = true)
    public PlaceListResponse getPlaceByTheme(ThemeRequest request){
        List<Place> places = placeRepository.findByTheme(CourseTheme.toEnum(request.getTheme()));

        List<PlaceResponse> responses = places.stream()
                .map(PlaceResponse::from)
                .toList();

        return PlaceListResponse.from(responses);
    }

}
