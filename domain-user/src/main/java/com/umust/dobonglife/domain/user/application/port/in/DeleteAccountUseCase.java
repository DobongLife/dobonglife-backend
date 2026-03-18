package com.umust.dobonglife.domain.user.application.port.in;

public interface DeleteAccountUseCase {

    void deleteAccount(Long userId);

    void handleDeletion(Long userId);
}
