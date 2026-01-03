package com.umust.dobonglife.domain.user.controller;

import com.umust.dobonglife.domain.user.controller.dto.response.MyPageResponse;
import com.umust.dobonglife.domain.user.service.MyPageApplicationService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.domain.user.controller.dto.request.SignupRequest;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final MyPageApplicationService myPageApplicationService;

    @PostMapping("/signup")
    public BaseResponse<Void> signUp(@RequestBody SignupRequest request) {
        userService.signUp(request);
        return BaseResponse.ok(null);
    }

    @PostMapping("/delete/account")
    public BaseResponse<Void> deleteAccount(HttpServletRequest request,
                                            @CurrentUserId Long userId) {
        userService.deleteAccount(request, userId);
        return BaseResponse.ok(null);
    }

    @GetMapping
    public BaseResponse<MyPageResponse> getMyPage(@CurrentUserId Long userId) {
        MyPageResponse response = myPageApplicationService.getMyPage(userId);
        return BaseResponse.ok(response);
    }
}

