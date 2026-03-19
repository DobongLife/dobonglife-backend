package com.umust.dobonglife.global.port.user.in;

public interface UpdatePasswordUseCase {
    void updateMyPassword(String email, String authCode, String newPassword);
}
