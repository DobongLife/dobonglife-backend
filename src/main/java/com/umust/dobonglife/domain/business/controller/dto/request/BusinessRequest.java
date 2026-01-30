package com.umust.dobonglife.domain.business.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessRequest {

    @Schema(description = "장소 아이디", example = "Optional입니다.")
    private Long placeId;

    @NotNull(message = "사업장명은 필수입니다")
    @Schema(description = "사업장명", example = "(주)유머스트알엔디")
    private String businessName;

    @NotNull(message = "사업장 소개는 필수입니다")
    @Schema(description = "사업장 소개", example = "서울특별시 도봉구 마들로")
    private String introduction;

    @NotNull(message = "전화번호는 필수입니다")
    @Schema(description = "전화번호", example = "02-123-4567")
    private String contact;

    @NotNull(message = "이메일은 필수입니다")
    @Schema(description = "이메일", example = "dobonglife@gmail.com")
    private String email;

    @NotNull(message = "주소은 필수입니다")
    @Schema(description = "주소", example = "서울특별시 도봉구 마들로")
    private String businessAddress;

    @Schema(description = "운영시간", example = "평일 09:00 ~ 18:00")
    private String operatingHour;

    @NotNull(message = "대표자 이름은 필수입니다")
    @Schema(description = "대표자 이름", example = "이강파")
    private String managerName;

    @NotNull(message = "사업자등롣번호는 필수입니다")
    @Schema(description = "사업자등롣번호", example = "2198701322")
    private String businessNumber;

    @NotNull(message = "위도는 필수입니다")
    @Schema(description = "위도", example = "21.98701322")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다")
    @Schema(description = "위도", example = "21.98701322")
    private Double longitude;

    @Schema(description = "카테고리", example = "ETC")
    private String businessCategory;

    @Schema(description = "주간테마", example = "NATURE")
    private List<String> themes;
}
