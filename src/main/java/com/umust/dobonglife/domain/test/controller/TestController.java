package com.umust.dobonglife.domain.test.controller;

import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/health-check")
    public BaseResponse<Void> test() {
        log.info("=== Test Controller test 진입 ===");
        return BaseResponse.ok(null);
    }

    @GetMapping("/error-check")
    public BaseResponse<Void> errorTest() {
        log.info("=== Test Controller errorTest 진입 ===");
        throw new BusinessException(ErrorCode.SERVER_ERROR);
    }
}

