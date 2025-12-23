package com.umust.dobonglife.domain.place.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceRegisterRequest {

    @Schema(description = "장소 이름", example = "도봉산 둘레길")
    private String placeName;

    @Schema(description = "장소 설명", example = "편안한 산책을 즐길 수 있는 곳입니다.")
    private String content;

    @Schema(description = "편의 시설", example = "PARKING, FOUNTAIN, TOILET, BENCH")
    private List<String> amenities;

    @Schema(description = "주소", example = "서울특별시 도봉구 도봉산 도봉산길 79")
    private String address;

    @Schema(description = "연락처", example = "02-123-4567")
    private String contact;

    @Schema(description = "운영 시간", example = "24시간 개방")
    private String operatingHour;
}
