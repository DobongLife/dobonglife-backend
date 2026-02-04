package com.umust.dobonglife.domain.place.service;

import com.umust.dobonglife.domain.business.controller.dto.request.BusinessRequest;
import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.domain.course.controller.dto.response.CourseSummaryResponse;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.service.CourseService;
import com.umust.dobonglife.domain.place.controller.dto.request.PlaceRegisterRequest;
import com.umust.dobonglife.domain.place.controller.dto.request.ThemeRequest;
import com.umust.dobonglife.domain.place.controller.dto.response.*;
import com.umust.dobonglife.domain.place.domain.constant.Amenity;
import com.umust.dobonglife.domain.place.domain.constant.PlaceCategory;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.entity.PlaceLike;
import com.umust.dobonglife.domain.place.domain.repository.PlaceLikeRepository;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;

import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import com.umust.dobonglife.global.common.webclient.business.dto.response.GeoPointResponse;
import com.umust.dobonglife.global.common.webclient.service.WebClientService;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import com.umust.dobonglife.global.external.s3.S3Utils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umust.dobonglife.global.common.model.BaseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final PlaceLikeRepository placeLikeRepository;
    private final S3Utils s3Utils;

    @Transactional
    public Long createPlaceForBusiness(BusinessRequest request, List<MultipartFile> imageFiles) {

        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new BusinessException(ErrorCode.PLACE_IMAGE_REQUIRED);
        }

        List<String> uploadedUrls = s3Utils.uploadImages(imageFiles);
        String thumbnailUrl = uploadedUrls.getFirst();

        Place place = Place.builder()
                .name(request.getBusinessName())
                .subName(request.getBusinessName())
                .category(PlaceCategory.toEnum(request.getCategory()))
                .content(request.getIntroduction())
                .address(request.getBusinessAddress())
                .operatingHour(request.getOperatingHour())
                .contact(request.getContact())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .imageUrls(uploadedUrls)
                .thumbnailUrl(thumbnailUrl)
                .themes(request.getThemes().stream()
                        .map(CourseTheme::toEnum)
                        .toList())
                .build();
        placeRepository.save(place);

        return place.getId();
    }


    @Transactional
    public void toggleLikes(Long userId, Long placeId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Place place = findById(placeId);

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

    public CursorResponse<PlaceSummaryResponse> getLikedPlace(Long userId, int size, Long lastId) {
        Pageable pageable = PageRequest.of(0, size);
        Slice<Place> slice = placeRepository.findLikedPlaceSummaries(userId, lastId, pageable);
        return CursorUtils.toCursorResponse(slice, place -> PlaceSummaryResponse.from(place, true, place.getThemes()));
    }

    @Transactional
    public void updatePlaceRatingAndCount(Long placeId, Double rating) {
        Place place = findById(placeId);
        place.applyNewReview(rating);
        placeRepository.save(place);
    }

    @Transactional
    public void deletePlaceReview(Long placeId, Double rating) {
        Place place = findById(placeId);
        place.applyDeleteReview(rating);
        placeRepository.save(place);
    }

    @Transactional
    public PlaceSummaryListResponse getAllPlace(Long userId) {
        List<PlaceSummaryResponse> responses = placeRepository.findPlaceSummaries(userId);
        return PlaceSummaryListResponse.from(responses);
    }

    public Place findById(Long placeId) {
        return placeRepository.findById(placeId).orElseThrow(() -> new BusinessException(ErrorCode.PLACE_NOT_FOUND));
    }

    @Transactional
    public Place resolvePlaceForUpdate(Business business, BusinessRequest request) {
        Place place = business.getPlace();

        place.setName(request.getBusinessName());
        place.setContent(request.getIntroduction());
        place.setAddress(request.getBusinessAddress());
        place.setContact(request.getContact());
        place.setOperatingHour(request.getOperatingHour() == null ? place.getOperatingHour() : request.getOperatingHour());
        place.setLatitude(request.getLatitude());
        place.setLongitude(request.getLongitude());
        place.setCategory(PlaceCategory.toEnum(request.getCategory()));

        place.setThemes(request.getThemes().stream()
                .map(CourseTheme::toEnum)
                .toList());

        return place;
    }

    public Long getLikedPlaceCount(Long userId) {
        return placeLikeRepository.countByUserId(userId);
    }
}
