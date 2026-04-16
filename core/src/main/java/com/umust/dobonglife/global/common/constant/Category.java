package com.umust.dobonglife.global.common.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Category {
    RESTAURANT("음식점", "https://s3.ap-northeast-2.amazonaws.com/dobong-img/images/3ea2e5af-e34e-46f3-943c-08b6c132253a.png"),
    CAFE("카페", "https://s3.ap-northeast-2.amazonaws.com/dobong-img/images/5c0f273b-947d-41b7-9563-fc8816771f51.png"),
    SHOPPING("쇼핑", "https://s3.ap-northeast-2.amazonaws.com/dobong-img/images/dd594b1b-de45-460a-b72c-2e2a78ffecce.png"),
    BEAUTY("뷰티", "https://s3.ap-northeast-2.amazonaws.com/dobong-img/images/3f049e5c-2fe0-4187-8b79-38ac1a4f1701.png"),
    FITNESS("피트니스", "https://s3.ap-northeast-2.amazonaws.com/dobong-img/images/5b508e07-0ae8-4c3a-b3da-bd8b6635134f.png"),
    MEDICAL_IT("의료/IT", "https://s3.ap-northeast-2.amazonaws.com/dobong-img/images/6f399c97-8daf-4a8a-b6de-c6d0e64b2c4b.png"),
    EXPERIENCE("체험", "https://s3.ap-northeast-2.amazonaws.com/dobong-img/images/c692c492-443d-446b-bbb2-f4ec5a0a79d4.png"),
    ETC("기타", "https://s3.ap-northeast-2.amazonaws.com/dobong-img/images/e282cf53-49d1-4ade-9fa6-481bc93a7bbb.png");

    private final String description;
    private final String imageUrl;
}
