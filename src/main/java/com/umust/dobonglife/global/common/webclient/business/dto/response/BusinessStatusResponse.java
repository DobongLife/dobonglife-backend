package com.umust.dobonglife.global.common.webclient.business.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
public class BusinessStatusResponse {
    @JsonProperty("status_code")
    private String statusCode;

    @JsonProperty("match_cnt")
    private int matchCount;

    @JsonProperty("request_cnt")
    private int requestCount;

    private List<BusinessData> data;
}