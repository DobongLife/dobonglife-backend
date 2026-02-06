package com.umust.dobonglife.domain.app.controller.dto.response;

import com.umust.dobonglife.domain.app.domain.constant.PolicyType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PolicyResponse {
    private PolicyType type;
    private String version;
    private String content;
}
