package com.umust.dobonglife.presentation.user.controller;

import com.umust.dobonglife.application.auth.AuthFacade;
import com.umust.dobonglife.presentation.user.dto.request.MailCodeCheckRequest;
import com.umust.dobonglife.presentation.user.dto.request.MailRequest;
import com.umust.dobonglife.presentation.user.dto.request.PasswordUpdateRequest;
import com.umust.dobonglife.domain.user.application.port.in.CheckAuthCodeUseCase;
import com.umust.dobonglife.domain.user.application.port.in.SendMailUseCase;
import com.umust.dobonglife.domain.user.application.port.in.SignUpUseCase;
import com.umust.dobonglife.domain.user.application.port.in.UpdatePasswordUseCase;
import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.umust.dobonglife.presentation.user.dto.request.SignupRequest;

@Tag(name = "사용자 API", description = "사용자 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final SignUpUseCase signUpUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;
    private final SendMailUseCase sendMailUseCase;
    private final CheckAuthCodeUseCase checkAuthCodeUseCase;
    private final AuthFacade authFacade;

    @Operation(summary = "회원 가입", description = "회원 가입을 합니다." +
            " role은 MEMBER, MANAGER, ADMIN 3개 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "회원가입에 성공하였습니다."
    )
    @PostMapping("/signup")
    public BaseResponse<Void> signUp(@Valid @RequestBody SignupRequest request) {
        signUpUseCase.signUp(request.getEmail(), request.getName(), request.getPassword());
        return BaseResponse.ok(null);
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "회원탈퇴에 성공하였습니다."
    )
    @PostMapping("/delete/account")
    public BaseResponse<Void> deleteAccount(HttpServletRequest request,
                                            @CurrentUserId Long userId){
        authFacade.deleteAccount(request, userId);
        return BaseResponse.ok(null);
    }

    @Operation(
            summary = "인증번호 전송",
            description = "메일로 인증번호를 전송합니다. isForSignUp이 True면 회원 가입 용도, 아닐 시 비밀번호 변경용입니다."
    )
    @PostMapping("/mail/send")
    public BaseResponse<Void> sendAuthCodeMail(@Valid @RequestBody MailRequest request) {
        sendMailUseCase.sendMail(request.getEmail(), request.isForSignUp());
        return BaseResponse.ok(null);
    }

    @Operation(
            summary = "인증번호 검증",
            description = "메일로 받은 인증번호를 검증합니다. isForSignUp이 True면 회원 가입 용도, 아닐 시 비밀번호 변경용입니다."
    )
    @PostMapping("/mail/check")
    public BaseResponse<Void> checkAuthCode(@Valid @RequestBody MailCodeCheckRequest request) {
        checkAuthCodeUseCase.checkAuthCode(request.getEmail(), request.getAuthCode(), request.isForSignUp());
        return BaseResponse.ok(null);
    }

    @Operation(
            summary = "비밀번호 변경",
            description = "비밀번호를 변경합니다."
    )
    @PatchMapping("/password")
    public BaseResponse<Void> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
        updatePasswordUseCase.updateMyPassword(request.getEmail(), request.getAuthCode(), request.getNewPassword());
        return BaseResponse.ok(null);
    }
}
