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
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import com.umust.dobonglife.global.common.s3.S3Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final CoursePlaceRepository coursePlaceRepository;
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

}
