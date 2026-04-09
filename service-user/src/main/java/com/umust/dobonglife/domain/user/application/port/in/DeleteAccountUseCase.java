package com.umust.dobonglife.domain.user.application.port.in;

public interface DeleteAccountUseCase {

    void markPending(Long userId);

    void deleteAccount(Long userId);

    void restoreAccount(Long userId);

    void handleDeletion(Long userId);
}
