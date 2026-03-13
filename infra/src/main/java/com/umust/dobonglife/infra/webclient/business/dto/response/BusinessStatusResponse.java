package com.umust.dobonglife.infra.webclient.business.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.umust.dobonglife.infra.webclient.business.data.BusinessData;
import lombok.*;

import java.util.List;

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
