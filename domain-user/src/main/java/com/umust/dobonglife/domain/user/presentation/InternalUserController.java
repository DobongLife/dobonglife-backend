package com.umust.dobonglife.domain.user.presentation;

import com.umust.dobonglife.domain.user.application.port.in.GetUserUseCase;
import com.umust.dobonglife.domain.user.application.port.in.ManageUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/user")
@RequiredArgsConstructor
public class InternalUserController {

    private final GetUserUseCase getUserUseCase;

    @GetMapping("/{userId}/blocked")
    public boolean isBlockedUser(@PathVariable Long userId) {
        return getUserUseCase.isBlockedUser(userId);
    }

    @GetMapping("/{userId}/fcm-token")
    public String getFcmToken(@PathVariable Long userId) {
        return getUserUseCase.getFcmToken(userId);
    }
}
