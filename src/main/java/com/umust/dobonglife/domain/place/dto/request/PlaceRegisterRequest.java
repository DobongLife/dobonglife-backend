package com.umust.dobonglife.domain.place.dto.request;

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

    private List<String> amenity;

    private String address;

    private String contact;

    private LocalDateTime operatingHour;
}
