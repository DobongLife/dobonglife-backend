package com.umust.dobonglife.domain.user.application.port.in;

public interface SignUpUseCase {

    void signUp(String email, String name, String password);
}
