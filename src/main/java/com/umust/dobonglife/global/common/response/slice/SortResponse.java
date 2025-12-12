package com.umust.dobonglife.global.common.response.slice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SortResponse {
    private final String by;
    private final String direction;
}

