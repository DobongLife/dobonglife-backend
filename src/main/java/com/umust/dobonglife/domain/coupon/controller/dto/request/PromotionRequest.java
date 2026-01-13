package com.umust.dobonglife.domain.coupon.controller.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionRequest {

    private String title;
    private String subTitle;
    private String themes;
    private String description;
    private String placeName;
    private String price;
    private String contact;
    private String hostName;
}
