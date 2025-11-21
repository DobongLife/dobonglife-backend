package com.umust.dobonglife.domain.place.service;

import com.umust.dobonglife.domain.place.dto.request.PlaceRegisterRequest;
import com.umust.dobonglife.domain.place.model.Amenity;
import com.umust.dobonglife.domain.place.model.Place;
import com.umust.dobonglife.domain.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    public void registerPlace(PlaceRegisterRequest request, List<MultipartFile> placeImageList){

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
                // .placeImages(placeImageList)
                .build();

        placeRepository.save(place);
    }

    public PlaceResponseList getPlaceByTheme(ThemeRequest request){


    }

}
