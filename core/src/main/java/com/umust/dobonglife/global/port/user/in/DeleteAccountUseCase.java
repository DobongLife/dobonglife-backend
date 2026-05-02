package com.umust.dobonglife.global.port.user.in;

public interface DeleteAccountUseCase {
    void deleteAccount(Long userId);
    void handleDeletion(Long userId);
}
