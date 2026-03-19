package com.umust.dobonglife.global.port.user.in;

public interface SendMailUseCase {
    void sendMail(String email, boolean forSignUp);
}
