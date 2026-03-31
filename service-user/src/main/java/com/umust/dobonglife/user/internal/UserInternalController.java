package com.umust.dobonglife.user.internal;

import com.umust.dobonglife.domain.user.application.port.in.DeleteAccountUseCase;
import com.umust.dobonglife.domain.user.application.port.in.GetUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.OAuthFindUserUseCase;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/users")
public class UserInternalController {

    private final DeleteAccountUseCase deleteAccountUseCase;
    private final GetUserUseCase getUserUseCase;
    private final ManageUserUseCase manageUserUseCase;
    private final OAuthFindUserUseCase oAuthFindUserUseCase;

    // Withdraw saga endpoints
    @PostMapping("/{userId}/mark-pending")
    public BaseResponse<Void> markPending(@PathVariable Long userId) {
        deleteAccountUseCase.markPending(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/{userId}/delete")
    public BaseResponse<Void> deleteAccount(@PathVariable Long userId) {
        deleteAccountUseCase.deleteAccount(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/{userId}/restore")
    public BaseResponse<Void> restoreAccount(@PathVariable Long userId) {
        deleteAccountUseCase.restoreAccount(userId);
        return BaseResponse.ok(null);
    }

    // User query endpoints (for other services)
    @GetMapping("/{userId}/provider")
    public BaseResponse<UserProviderResponse> getUserProvider(@PathVariable Long userId) {
        return BaseResponse.ok(new UserProviderResponse(
                getUserUseCase.getProvider(userId).name(),
                getUserUseCase.getProviderId(userId)
        ));
    }

    @GetMapping("/{userId}/active")
    public BaseResponse<Boolean> isActiveUser(@PathVariable Long userId) {
        return BaseResponse.ok(!getUserUseCase.isNotActiveUser(userId));
    }

    @GetMapping("/{userId}/blocked")
    public BaseResponse<Boolean> isBlockedUser(@PathVariable Long userId) {
        return BaseResponse.ok(getUserUseCase.isBlockedUser(userId));
    }

    @GetMapping("/{userId}/fcm-token")
    public BaseResponse<String> getFcmToken(@PathVariable Long userId) {
        return BaseResponse.ok(getUserUseCase.getFcmToken(userId));
    }

    @PostMapping("/{userId}/fcm-token/invalidate")
    public BaseResponse<Void> invalidateFcmToken(@PathVariable Long userId) {
        manageUserUseCase.inValidFcmToken(userId);
        return BaseResponse.ok(null);
    }

    @PostMapping("/{userId}/fcm-token")
    public BaseResponse<Void> updateFcmToken(@PathVariable Long userId, @RequestParam String fcmToken) {
        manageUserUseCase.updateFcmToken(userId, fcmToken);
        return BaseResponse.ok(null);
    }

    @PostMapping("/{userId}/can-exchange")
    public BaseResponse<Void> canExchangeCoupon(@PathVariable Long userId) {
        manageUserUseCase.canExchangeCoupon(userId);
        return BaseResponse.ok(null);
    }

    // OAuth user find/create (for auth-service)
    @PostMapping("/oauth/find-or-create")
    public BaseResponse<OAuthUserResponse> findOrCreateOAuthUser(@RequestBody OAuthUserRequest request) {
        var user = oAuthFindUserUseCase.findOrCreateOAuthUser(
                Provider.valueOf(request.provider()), request.providerId(), request.email(), request.name()
        );
        return BaseResponse.ok(new OAuthUserResponse(user.id(), user.role().name(), user.name()));
    }

    public record UserProviderResponse(String provider, String providerId) {}
    public record OAuthUserRequest(String provider, String providerId, String email, String name) {}
    public record OAuthUserResponse(Long id, String role, String name) {}
}
