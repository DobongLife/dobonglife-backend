package com.umust.dobonglife.infra.s3;

import com.umust.dobonglife.global.common.image.ImageUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class NoOpImageUploader implements ImageUploader {

    @Override
    public List<String> uploadImages(List<MultipartFile> images) {
        log.warn("S3 미설정 — 이미지 업로드가 비활성화되어 있습니다");
        return Collections.emptyList();
    }

    @Override
    public void deleteImages(List<String> imageUrls) {
        log.warn("S3 미설정 — 이미지 삭제가 비활성화되어 있습니다");
    }
}
