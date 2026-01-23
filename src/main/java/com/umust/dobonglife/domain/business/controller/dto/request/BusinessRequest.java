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

    @NotNull(message = "종료 시간은 필수입니다")
    @Schema(description = "종료 시간", example = "2025-01-10T16:00:00")
    private String businessName;

    @NotNull(message = "종료 시간은 필수입니다")
    @Schema(description = "종료 시간", example = "2025-01-10T16:00:00")
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
