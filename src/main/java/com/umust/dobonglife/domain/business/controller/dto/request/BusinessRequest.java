package com.umust.dobonglife.domain.business.controller.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessRequest {

    private String businessName;

    private String businessAddress;

    private String introduction;

    private String contact;

    private String email;

    private String link;

    private String operatingHour;

    private String mangerName;

    private String businessNumber;

    private String category;

    private List<String> service;
}
