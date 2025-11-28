package com.umust.dobonglife.domain.place.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Theme {
    NATURE("Nature"),
    CULTURE("Culture"),
    RESTAURANT("Restaurant"),
    HISTORY("History"),
    FAMILY("Family"),
    ACTIVITY("Activity");

    private final String value;
}
