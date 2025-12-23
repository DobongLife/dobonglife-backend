package com.umust.dobonglife.domain.business.domain.constant;

public enum BusinessService {
    DELIVERY,
    TAKE_OUT,
    PARKING,
    WIFI,
    CARD,
    RESERVATION,
    GROUP,
    DISABLED;

    public static BusinessService toEnum(String value) {
        for (BusinessService amenity : BusinessService.values()) {
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
