package com.umust.dobonglife.domain.place.domain.constant;

public enum Amenity {
    PARKING,
    TOILET,
    FOUNTAIN,
    BENCH;

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
