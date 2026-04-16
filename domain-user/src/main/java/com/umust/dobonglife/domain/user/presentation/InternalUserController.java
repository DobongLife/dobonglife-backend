package com.umust.dobonglife.domain.user.presentation;

import com.umust.dobonglife.domain.user.application.dto.LocalLoginUser;
import com.umust.dobonglife.domain.user.application.dto.OAuthLoginUser;
import com.umust.dobonglife.domain.business.application.port.in.CheckBusinessUseCase;
import com.umust.dobonglife.domain.business.application.port.in.GetBusinessCategoryUseCase;
import com.umust.dobonglife.domain.user.application.port.in.CheckAuthCodeUseCase;
import com.umust.dobonglife.domain.user.application.port.in.DeleteAccountUseCase;
import com.umust.dobonglife.domain.user.application.port.in.GetUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.LoadLocalAuthUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.OAuthFindUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.SendMailUseCase;
import com.umust.dobonglife.domain.user.application.port.in.SignUpUseCase;
import com.umust.dobonglife.domain.user.application.port.in.UpdatePasswordUseCase;
import com.umust.dobonglife.domain.user.presentation.dto.request.AuthCodeCheckRequest;
import com.umust.dobonglife.domain.user.presentation.dto.request.MailSendRequest;
import com.umust.dobonglife.domain.user.presentation.dto.request.OAuthFindOrCreateUserRequest;
import com.umust.dobonglife.domain.user.presentation.dto.request.PasswordUpdateRequest;
import com.umust.dobonglife.domain.user.presentation.dto.request.SignUpRequest;
import com.umust.dobonglife.domain.user.presentation.dto.request.TokenUpdateRequest;
import com.umust.dobonglife.global.common.constant.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/user")
@RequiredArgsConstructor
public class InternalUserController {

    private final CheckBusinessUseCase checkBusinessUseCase;
    private final GetBusinessCategoryUseCase getBusinessCategoryUseCase;
    private final GetUserUseCase getUserUseCase;
    private final SignUpUseCase signUpUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;
    private final SendMailUseCase sendMailUseCase;
    private final CheckAuthCodeUseCase checkAuthCodeUseCase;
    private final LoadLocalAuthUserUseCase loadLocalAuthUserUseCase;
    private final OAuthFindUserUseCase oAuthFindUserUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;
    private final ManageUserUseCase manageUserUseCase;

    @GetMapping("/{userId}/blocked")
    public boolean isBlockedUser(@PathVariable Long userId) {
        return getUserUseCase.isBlockedUser(userId);
    }

    @GetMapping("/{userId}/fcm-token")
    public String getFcmToken(@PathVariable Long userId) {
        return getUserUseCase.getFcmToken(userId);
    }

    @GetMapping("/{userId}/provider")
    public String getProvider(@PathVariable Long userId) {
        return getUserUseCase.getProvider(userId).name();
    }

    @GetMapping("/{userId}/provider-id")
    public String getProviderId(@PathVariable Long userId) {
        return getUserUseCase.getProviderId(userId);
    }

    @GetMapping("/{userId}/provider-token")
    public String getProviderToken(@PathVariable Long userId) {
        return getUserUseCase.getProviderToken(userId);
    }

    @GetMapping("/{userId}/business")
    public boolean hasBusiness(@PathVariable Long userId) {
        return checkBusinessUseCase.checkBusiness(userId);
    }

    @GetMapping("/{userId}/business/category")
    public String getBusinessCategory(@PathVariable Long userId) {
        return getBusinessCategoryUseCase.getBusinessCategory(userId).name();
    }

    @GetMapping("/local-auth")
    public LocalLoginUser loadLocalUser(@RequestParam String email) {
        return loadLocalAuthUserUseCase.loadLocalUserByEmail(email);
    }

    @GetMapping("/auth-code/signup")
    public String getStoredSignUpCode(@RequestParam String email) {
        return checkAuthCodeUseCase.getStoredSignUpCode(email);
    }

    @GetMapping("/auth-code/password")
    public String getStoredPasswordCode(@RequestParam String email) {
        return checkAuthCodeUseCase.getStoredPasswordCode(email);
    }

    @PostMapping("/signup")
    public void signUp(@RequestBody SignUpRequest request) {
        signUpUseCase.signUp(request.email(), request.name(), request.password());
    }

    @PostMapping("/mail/send")
    public void sendMail(@RequestBody MailSendRequest request) {
        sendMailUseCase.sendMail(request.email(), request.forSignUp());
    }

    @PostMapping("/mail/check")
    public void checkAuthCode(@RequestBody AuthCodeCheckRequest request) {
        checkAuthCodeUseCase.checkAuthCode(request.email(), request.authCode(), request.forSignUp());
    }

    @PatchMapping("/password")
    public void updatePassword(@RequestBody PasswordUpdateRequest request) {
        updatePasswordUseCase.updateMyPassword(request.email(), request.authCode(), request.newPassword());
    }

    @PostMapping("/oauth/find-or-create")
    public OAuthLoginUser findOrCreateOAuthUser(@RequestBody OAuthFindOrCreateUserRequest request) {
        return oAuthFindUserUseCase.findOrCreateOAuthUser(
                Provider.valueOf(request.provider()),
                request.providerUserId(),
                request.email(),
                request.name()
        );
    }

    @DeleteMapping("/{userId}")
    public void deleteAccount(@PathVariable Long userId) {
        deleteAccountUseCase.deleteAccount(userId);
    }

    @PostMapping("/{userId}/deletion/handle")
    public void handleDeletion(@PathVariable Long userId) {
        deleteAccountUseCase.handleDeletion(userId);
    }

    @PostMapping("/{userId}/mark-pending")
    public void markPending(@PathVariable Long userId) {
        deleteAccountUseCase.markPending(userId);
    }

    @PostMapping("/{userId}/restore")
    public void restoreAccount(@PathVariable Long userId) {
        deleteAccountUseCase.restoreAccount(userId);
    }

    @PostMapping("/{userId}/exchange/check")
    public void canExchangeCoupon(@PathVariable Long userId) {
        manageUserUseCase.canExchangeCoupon(userId);
    }

    @PatchMapping("/{userId}/notification-setting")
    public void updateNotificationSetting(@PathVariable Long userId, @RequestParam boolean enabled) {
        manageUserUseCase.updateNotificationSetting(userId, enabled);
    }

    @PatchMapping("/{userId}/fcm-token")
    public void updateFcmToken(@PathVariable Long userId, @RequestBody TokenUpdateRequest request) {
        manageUserUseCase.updateFcmToken(userId, request.token());
    }

    @PatchMapping("/{userId}/provider-token")
    public void updateProviderToken(@PathVariable Long userId, @RequestBody TokenUpdateRequest request) {
        manageUserUseCase.updateProviderToken(userId, request.token());
    }

    @DeleteMapping("/{userId}/fcm-token")
    public void invalidateFcmToken(@PathVariable Long userId) {
        manageUserUseCase.inValidFcmToken(userId);
    }
}
