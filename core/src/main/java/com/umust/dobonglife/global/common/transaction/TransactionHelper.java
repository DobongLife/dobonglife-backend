package com.umust.dobonglife.global.common.transaction;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionHelper {

    public static void onRollback(Runnable action) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK) {
                            action.run();
                        }
                    }
                }
        );
    }
}
