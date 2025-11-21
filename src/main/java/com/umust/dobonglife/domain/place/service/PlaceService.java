package com.umust.dobonglife.domain.place.service;

import com.umust.dobonglife.domain.place.dto.request.PlaceRegisterRequest;
import com.umust.dobonglife.domain.place.model.Place;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    public void registerPlace(PlaceRegisterRequest request){

        Place place = Place.builder()







                .build();

    }
}
