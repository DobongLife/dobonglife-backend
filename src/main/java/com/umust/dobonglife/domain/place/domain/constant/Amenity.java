package com.umust.dobonglife.domain.place.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Amenity {
    PARKING("주차장"),
    TOILET("화장싱"),
    FOUNTAIN("분수대"),
    BENCH("벤치");

    private final String label;

    public static Amenity toEnum(String value) {
        for (Amenity amenity : Amenity.values()) {
            if (amenity.name().equalsIgnoreCase(value)) {
                return amenity;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 enum입니다: " + value);
    }

    public String toValue() {
        return name();
    }
}
