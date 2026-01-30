package com.umust.dobonglife.global.common;

import com.umust.dobonglife.domain.point.controller.PointController;
import com.umust.dobonglife.domain.point.controller.dto.response.PointPageResponse;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.external.s3.S3Utils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.util.ImageUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {
    private final S3Utils imageUtils;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<List<String>> uploadPromotionImages(
            @RequestPart("images") List<MultipartFile> images) {
        List<String> imageUrls = imageUtils.uploadImages(images);
        return BaseResponse.ok(imageUrls);
    }


}
