package com.umust.dobonglife.domain.business.domain.constant;

public enum BusinessCategory {
    RESTAURANT,
    CAFE,
    SHOPPING,
    CULTURE,
    EDUCATION,
    MEDICAL,
    BEAUTY,
    FITNESS,
    EXPERIENCE,
    ETC;


    public static BusinessCategory toEnum(String value) {
        for (BusinessCategory category : BusinessCategory.values()) {
            if (category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 enum입니다: " + value);
    }

    public String toValue() {
        return name();
    }
}
