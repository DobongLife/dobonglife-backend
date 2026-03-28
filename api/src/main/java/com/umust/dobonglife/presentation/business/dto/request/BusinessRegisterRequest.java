package com.umust.dobonglife.presentation.business.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessRegisterRequest {

    @Schema(description = "장소 아이디", example = "Optional입니다.")
    private Long placeId;

    @Schema(description = "장소 부제", example = "도봉구 최고의 명소입니다.")
    private String subName;

    @NotNull(message = "사업장명은 필수입니다")
    @Schema(description = "사업장명", example = "(주)유머스트알엔디")
    private String businessName;

    @Schema(description = "사업장 소개", example = "서울특별시 도봉구 마들로")
    private String content;

    @Schema(description = "전화번호", example = "02-123-4567")
    private String contact;

    @NotNull(message = "이메일은 필수입니다")
    @Schema(description = "이메일", example = "dobonglife@gmail.com")
    private String email;

    @Schema(description = "주소", example = "서울특별시 도봉구 마들로")
    private String businessAddress;

    @Schema(description = "운영시간", example = "평일 09:00 ~ 18:00")
    private String operatingHour;

    @NotNull(message = "대표자 이름은 필수입니다")
    @Schema(description = "대표자 이름", example = "이강파")
    private String managerName;

    @NotNull(message = "사업자등록번호는 필수입니다")
    @Schema(description = "사업자등록번호", example = "2198701322")
    private String businessNumber;

    @Schema(description = "위도", example = "21.98701322")
    private Double latitude;

    @Schema(description = "경도", example = "21.98701322")
    private Double longitude;

    @Schema(description = "카테고리", example = "CAFE")
    private String category;

    @Schema(description = "주간테마", example = "NATURE")
    private List<String> themes;
}
