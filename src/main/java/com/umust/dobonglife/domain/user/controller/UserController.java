package com.umust.dobonglife.domain.user.controller;

import com.umust.dobonglife.domain.user.controller.dto.response.MyPageResponse;
import com.umust.dobonglife.domain.user.service.MyPageApplicationService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.umust.dobonglife.domain.user.service.UserService;
import com.umust.dobonglife.domain.user.controller.dto.request.SignupRequest;

@Tag(name = "사용자 API", description = "사용자 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final MyPageApplicationService myPageApplicationService;

    @Operation(summary = "회원 가입", description = "회원 가입을 합니다." +
            " role은 MEMBER, MANAGER, ADMIN 3개 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "회원가입에 성공하였습니다."
    )
    @PostMapping("/signup")
    public BaseResponse<Void> signUp(@RequestBody SignupRequest request) {
        userService.signUp(request);
        return BaseResponse.ok(null);
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "회원탈퇴에 성공하였습니다."
    )
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

