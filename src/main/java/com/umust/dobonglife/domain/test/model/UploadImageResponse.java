package com.umust.dobonglife.domain.test.model;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UploadImageResponse(List<String> images) {
}
