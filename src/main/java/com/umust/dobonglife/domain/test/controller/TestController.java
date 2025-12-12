package com.umust.dobonglife.domain.test.controller;

import com.umust.dobonglife.domain.auth.model.UserPrincipal;
import com.umust.dobonglife.domain.test.model.Test;
import com.umust.dobonglife.domain.test.model.UploadImageRequest;
import com.umust.dobonglife.domain.test.model.UploadImageResponse;
import com.umust.dobonglife.domain.test.repository.TestRepository;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.ErrorCode;
import com.umust.dobonglife.global.common.s3.S3Utils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {

    private final TestRepository testRepository;
    private final S3Utils s3Utils;

    @GetMapping("/health-check")
    public BaseResponse<Void> test() {
        log.info("=== Test Controller test 진입 ===");
        return BaseResponse.ok(null);
    }

    @PostMapping("/save")
    public BaseResponse<Void> dbTest() {
        log.info("=== Test Controller test 진입 ===");
        testRepository.save(new Test());
        return BaseResponse.ok(null);
    }

    @GetMapping("/error-check")
    public BaseResponse<Void> errorTest() {
        log.info("=== Test Controller errorTest 진입 ===");
        throw new BusinessException(ErrorCode.SERVER_ERROR);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<UploadImageResponse> uploadImageUser(@ModelAttribute @Valid UploadImageRequest uploadImageRequest){
        List<String> imageUrls = s3Utils.uploadImages(uploadImageRequest.images());
        UploadImageResponse response = new UploadImageResponse(imageUrls);
        return BaseResponse.ok(response);
    }
}

