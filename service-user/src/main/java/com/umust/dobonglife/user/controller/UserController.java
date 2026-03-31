package com.umust.dobonglife.user.controller;

import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.domain.user.application.port.in.CheckAuthCodeUseCase;
import com.umust.dobonglife.domain.user.application.port.in.SendMailUseCase;
import com.umust.dobonglife.domain.user.application.port.in.SignUpUseCase;
import com.umust.dobonglife.domain.user.application.port.in.UpdatePasswordUseCase;
import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.user.withdraw.WithdrawOrchestrator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final SignUpUseCase signUpUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;
    private final SendMailUseCase sendMailUseCase;
    private final CheckAuthCodeUseCase checkAuthCodeUseCase;
    private final WithdrawOrchestrator withdrawOrchestrator;
    private final TokenExtractor tokenExtractor;

    @PostMapping("/signup")
    public BaseResponse<Void> signUp(@Valid @RequestBody SignupRequest request) {
        signUpUseCase.signUp(request.email(), request.name(), request.password());
        return BaseResponse.ok(null);
    }

    @PostMapping("/delete/account")
    public BaseResponse<Void> deleteAccount(HttpServletRequest request, @CurrentUserId Long userId) {
        String accessToken = tokenExtractor.extractAccessToken(request);
        String refreshToken = tokenExtractor.extractRefreshTokenOptional(request).orElse(null);
        withdrawOrchestrator.execute(userId, accessToken, refreshToken);
        return BaseResponse.ok(null);
    }

    @PostMapping("/mail/send")
    public BaseResponse<Void> sendAuthCodeMail(@Valid @RequestBody MailRequest request) {
        sendMailUseCase.sendMail(request.email(), request.isForSignUp());
        return BaseResponse.ok(null);
    }

    @PostMapping("/mail/check")
    public BaseResponse<Void> checkAuthCode(@Valid @RequestBody MailCodeCheckRequest request) {
        checkAuthCodeUseCase.checkAuthCode(request.email(), request.authCode(), request.isForSignUp());
        return BaseResponse.ok(null);
    }

    @PatchMapping("/password")
    public BaseResponse<Void> updatePassword(@Valid @RequestBody PasswordUpdateRequest request) {
        updatePasswordUseCase.updateMyPassword(request.email(), request.authCode(), request.newPassword());
        return BaseResponse.ok(null);
    }

    public record SignupRequest(
            @NotBlank String email,
            @NotBlank String name,
            @NotBlank String password
    ) {}

    public record MailRequest(
            @NotBlank String email,
            boolean isForSignUp
    ) {}

    public record MailCodeCheckRequest(
            @NotBlank String email,
            @NotBlank String authCode,
            boolean isForSignUp
    ) {}

    public record PasswordUpdateRequest(
            @NotBlank String email,
            @NotBlank String authCode,
            @NotBlank String newPassword
    ) {}
}
