package com.umust.dobonglife.domain.review.domain.constant;

public enum Template {
   GREAT, FAMILIAR, HEALING, KIND, PHOTO, RETRY;

    public static Template toEnum(String value) {
        for (Template template : Template.values()) {
            if (template.name().equalsIgnoreCase(value)) {
                return template;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 enum입니다: " + value);
    }
}