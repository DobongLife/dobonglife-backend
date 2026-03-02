package com.umust.dobonglife.global.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Identifiable {
    @JsonIgnore
    Long getId();
}
