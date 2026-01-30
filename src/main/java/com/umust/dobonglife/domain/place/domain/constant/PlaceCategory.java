package com.umust.dobonglife.domain.place.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlaceCategory {
    RESTAURANT("음식점"),
    CAFE("카페"),
    SHOPPING("쇼핑"),
    MEDICAL("의료/IT"),
    BEAUTY("뷰티"),
    FITNESS("피트니스"),
    LANDMARK("명소"),
    ETC("기타");

    private final String value;

    public static PlaceCategory toEnum(String value) {
        for (PlaceCategory category : PlaceCategory.values()) {
            if (category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 enum입니다: " + value);
    }
}
