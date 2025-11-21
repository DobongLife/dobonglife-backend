package com.umust.dobonglife.domain.user.controller;

import com.umust.dobonglife.global.common.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.domain.user.dto.request.SignupRequest;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

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
}

