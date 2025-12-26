package com.umust.dobonglife.domain.business.controller.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessRequest {

    private String businessName;

    private String businessAddress;

    private String introduction;

    private String phoneNumber;

    private String email;

    private String link;

    private String operatingHour;

    private String managerName;

    private String businessNumber;

    private String businessCategory;

    private List<String> businessService;
}
