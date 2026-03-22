package com.umust.dobonglife.global.image;

import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.infra.s3.S3Utils;
import com.umust.dobonglife.infra.s3.S3Utils.PresignedUrlResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {

    private final S3Utils s3Utils;

    @PostMapping("/presigned-url")
    public BaseResponse<List<PresignedUrlResult>> getPresignedUrls(
            @RequestBody PresignedUrlRequest request) {
        return BaseResponse.ok(s3Utils.generatePresignedUrls(request.extensions()));
    }

    public record PresignedUrlRequest(List<String> extensions) {}
}
