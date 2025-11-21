package com.umust.dobonglife.domain.place.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceRegisterRequest {

    private String placeName;

    private String content;

    private String amenity;

    private String address;

    private String contact;

    private LocalDateTime operatingHour;
}
