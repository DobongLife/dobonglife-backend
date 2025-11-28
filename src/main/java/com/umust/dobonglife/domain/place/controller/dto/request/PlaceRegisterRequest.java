package com.umust.dobonglife.domain.place.controller.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceRegisterRequest {

    private String placeName;

    private String content;

    private List<String> amenities;

    private String address;

    private String contact;

    private String operatingHour;
}
