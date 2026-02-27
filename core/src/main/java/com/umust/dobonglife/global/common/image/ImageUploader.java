package com.umust.dobonglife.global.common.image;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageUploader {

    List<String> uploadImages(List<MultipartFile> images);

    void deleteImages(List<String> imageUrls);
}
