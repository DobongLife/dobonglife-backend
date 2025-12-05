package com.umust.dobonglife.domain.place.service;


import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.repository.CoursePlaceRepository;
import com.umust.dobonglife.domain.place.controller.dto.request.PlaceRegisterRequest;
import com.umust.dobonglife.domain.place.controller.dto.request.ThemeRequest;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceResponse;
import com.umust.dobonglife.domain.place.controller.dto.response.PlaceResponseList;
import com.umust.dobonglife.domain.place.domain.constant.Amenity;
import com.umust.dobonglife.domain.place.domain.entity.CoursePlace;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.entity.PlaceLike;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final CoursePlaceRepository coursePlaceRepository;
    private final UserRepository userRepository;
    private final S3Utils s3Utils;

    @Transactional
    public void registerPlace(PlaceRegisterRequest request){

        List<String> images = s3Utils.uploadImages(request.getImageUrls());

        Place place = Place.builder()
                .name(request.getPlaceName())
                .content(request.getContent())
                .amenities(request.getAmenities()
                        .stream()
                        .map(Amenity::toEnum)
                        .toList())
                .address(request.getAddress())
                .contact(request.getContact())
                .operatingHour(request.getOperatingHour())
                .placeImages(images)
                .build();

        placeRepository.save(place);
    }

    @Transactional(readOnly = true)
    public PlaceResponseList getPlaceByTheme(ThemeRequest request){
        List<CoursePlace> coursePlaces = coursePlaceRepository.findByTheme(CourseTheme.toEnum(request.getTheme()));

        List<Place> places = coursePlaces.stream()
                .map(CoursePlace::getPlace)
                .distinct()
                .toList();

        List<PlaceResponse> responses = places.stream()
                .map(PlaceResponse::from)
                .toList();

        return PlaceResponseList.from(responses);
    }

    @Transactional
    public boolean toggleLikes(Long userId, Long placeId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));

        Optional<PlaceLike> articleLikesOptional = articleLikesRepository.findByMemberIdAndArticleId(member.getId(), article.getId());

        if(articleLikesOptional.isPresent()) {
            PlaceLike articleLikes = articleLikesOptional.get();

            if(articleLikes.getStatus() == Status.DELETED) {
                articleLikes.restore();
                return true;
            }
            else {
                articleLikes.softDelete();
                return false;
            }
        }

        PlaceLike articleLikes = ArticleLikes.builder()
                .member(member)
                .article(article)
                .build();
        articleLikesRepository.save(articleLikes);
        return true;
    }


}
