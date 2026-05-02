package com.umust.dobonglife.internal.withdraw;

import com.umust.dobonglife.application.withdraw.WithdrawOrchestrator;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/withdraw")
public class WithdrawInternalController {

    private final WithdrawOrchestrator withdrawOrchestrator;

    @PostMapping("/{userId}")
    public BaseResponse<Void> withdraw(
            @PathVariable Long userId,
            @RequestParam String accessToken,
            @RequestParam(required = false) String refreshToken) {
        withdrawOrchestrator.execute(userId, accessToken, refreshToken);
        return BaseResponse.ok(null);
    }
}
