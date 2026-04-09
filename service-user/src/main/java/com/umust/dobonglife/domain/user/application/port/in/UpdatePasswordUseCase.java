package com.umust.dobonglife.domain.user.application.port.in;

public interface UpdatePasswordUseCase {

    void updateMyPassword(String email, String authCode, String newPassword);
}
