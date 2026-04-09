package com.umust.dobonglife.domain.user.application.port.in;

public interface SendMailUseCase {

    void sendMail(String email, boolean forSignUp);
}
