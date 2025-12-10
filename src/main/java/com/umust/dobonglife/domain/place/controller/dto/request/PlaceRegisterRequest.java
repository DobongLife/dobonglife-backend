package com.umust.dobonglife.domain.place.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceRegisterRequest {

    private String placeName;

    private String content;

    private List<String> amenities;

    private String address;

    private String contact;

    private String operatingHour;

    @NotEmpty(message = "이미지는 최소 1개 이상 필수입니다.")
    @Size(min = 1, max = 5, message = "이미지는 1개 이상 5개 이하로 등록해야 합니다.")
    private List<MultipartFile> images;
}
