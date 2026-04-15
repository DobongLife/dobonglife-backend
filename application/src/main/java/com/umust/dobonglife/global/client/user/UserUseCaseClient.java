package com.umust.dobonglife.global.client.user;

import com.umust.dobonglife.global.client.config.InternalRestClientFactory;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.port.user.dto.LocalLoginUser;
import com.umust.dobonglife.global.port.user.dto.OAuthLoginUser;
import com.umust.dobonglife.global.port.user.in.CheckAuthCodeUseCase;
import com.umust.dobonglife.global.port.user.in.DeleteAccountUseCase;
import com.umust.dobonglife.global.port.user.in.LoadLocalAuthUserUseCase;
import com.umust.dobonglife.global.port.user.in.ManageUserUseCase;
import com.umust.dobonglife.global.port.user.in.OAuthFindUserUseCase;
import com.umust.dobonglife.global.port.user.in.SendMailUseCase;
import com.umust.dobonglife.global.port.user.in.SignUpUseCase;
import com.umust.dobonglife.global.port.user.in.UpdatePasswordUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class UserUseCaseClient implements
        SignUpUseCase,
        UpdatePasswordUseCase,
        SendMailUseCase,
        CheckAuthCodeUseCase,
        LoadLocalAuthUserUseCase,
        OAuthFindUserUseCase,
        DeleteAccountUseCase,
        ManageUserUseCase {

    private final RestClient restClient;

    public UserUseCaseClient(@Value("${service.user.url}") String baseUrl) {
        this.restClient = InternalRestClientFactory.create(baseUrl, "user-service");
    }

    @Override
    public void signUp(String email, String name, String password) {
        restClient.post()
                .uri("/internal/user/signup")
                .body(Map.of("email", email, "name", name, "password", password))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void updateMyPassword(String email, String authCode, String newPassword) {
        restClient.patch()
                .uri("/internal/user/password")
                .body(Map.of("email", email, "authCode", authCode, "newPassword", newPassword))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void sendMail(String email, boolean forSignUp) {
        restClient.post()
                .uri("/internal/user/mail/send")
                .body(Map.of("email", email, "forSignUp", forSignUp))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void checkAuthCode(String email, String authCode, boolean forSignUp) {
        restClient.post()
                .uri("/internal/user/mail/check")
                .body(Map.of("email", email, "authCode", authCode, "forSignUp", forSignUp))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void checkPasswordAuthCode(String email, String authCode) {
        restClient.post()
                .uri("/internal/user/mail/check")
                .body(Map.of("email", email, "authCode", authCode, "forSignUp", false))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public String getStoredSignUpCode(String email) {
        return restClient.get()
                .uri("/internal/user/auth-code/signup?email={email}", email)
                .retrieve()
                .body(String.class);
    }

    @Override
    public String getStoredPasswordCode(String email) {
        return restClient.get()
                .uri("/internal/user/auth-code/password?email={email}", email)
                .retrieve()
                .body(String.class);
    }

    @Override
    public LocalLoginUser loadLocalUserByEmail(String email) {
        return restClient.get()
                .uri("/internal/user/local-auth?email={email}", email)
                .retrieve()
                .body(LocalLoginUser.class);
    }

    @Override
    public OAuthLoginUser findOrCreateOAuthUser(Provider provider, String providerUserId, String email, String name) {
        return restClient.post()
                .uri("/internal/user/oauth/find-or-create")
                .body(Map.of(
                        "provider", provider.name(),
                        "providerUserId", providerUserId,
                        "email", email,
                        "name", name == null ? "" : name
                ))
                .retrieve()
                .body(OAuthLoginUser.class);
    }

    @Override
    public void deleteAccount(Long userId) {
        restClient.delete()
                .uri("/internal/user/{userId}", userId)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void handleDeletion(Long userId) {
        restClient.post()
                .uri("/internal/user/{userId}/deletion/handle", userId)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void canExchangeCoupon(Long userId) {
        restClient.post()
                .uri("/internal/user/{userId}/exchange/check", userId)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void updateNotificationSetting(Long userId, boolean enabled) {
        restClient.patch()
                .uri("/internal/user/{userId}/notification-setting?enabled={enabled}", userId, enabled)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void updateFcmToken(Long userId, String fcmToken) {
        restClient.patch()
                .uri("/internal/user/{userId}/fcm-token", userId)
                .body(Map.of("token", fcmToken))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void updateProviderToken(Long userId, String providerToken) {
        restClient.patch()
                .uri("/internal/user/{userId}/provider-token", userId)
                .body(Map.of("token", providerToken))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void inValidFcmToken(Long userId) {
        restClient.delete()
                .uri("/internal/user/{userId}/fcm-token", userId)
                .retrieve()
                .toBodilessEntity();
    }
}
