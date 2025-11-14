package com.umust.dobonglife.domain.test.controller;

import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.common.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {

    private final TestRepository testRepository;

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
}

