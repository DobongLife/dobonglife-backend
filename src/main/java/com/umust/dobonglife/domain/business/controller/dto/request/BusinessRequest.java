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

    @NotNull(message = "상호명은 필수입니다")
    @Schema(description = "상호명", example = "(주)유머스트알엔디")
    private String businessName;

    @NotNull(message = "주소은 필수입니다")
    @Schema(description = "주소", example = "서울특별시 도봉구 마들로")
    private String businessAddress;

    private String introduction;

    private String phoneNumber;

    private String email;

    private String link;

    private String operatingHour;

    @NotNull(message = "대표자 이름은 필수입니다")
    @Schema(description = "대표자 이름", example = "이강파")
    private String managerName;

    @NotNull(message = "주소은 필수입니다")
    @Schema(description = "주소", example = "서울특별시 도봉구 마들로")
    private String businessNumber;

    private String businessCategory;

    private List<String> businessService;
}
