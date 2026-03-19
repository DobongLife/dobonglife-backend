package com.umust.dobonglife.global.port.user.in;

public interface CheckAuthCodeUseCase {
    void checkAuthCode(String email, String authCode, boolean forSignUp);
    void checkPasswordAuthCode(String email, String authCode);
    String getStoredSignUpCode(String email);
    String getStoredPasswordCode(String email);
}
