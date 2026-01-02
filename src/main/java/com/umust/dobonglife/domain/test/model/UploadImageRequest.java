package com.umust.dobonglife.domain.test.model;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UploadImageRequest(@NotNull(message = "이미지는 필수 입력 항목입니다.")
                                 List<MultipartFile> images) {
}
