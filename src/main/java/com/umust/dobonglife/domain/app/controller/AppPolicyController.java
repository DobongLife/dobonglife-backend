package com.umust.dobonglife.domain.app.controller;

import com.umust.dobonglife.domain.app.controller.dto.response.SettingResponse;
import com.umust.dobonglife.domain.app.service.AppPolicyService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class AppPolicyController {
    private final AppPolicyService policyService;

    @GetMapping
    public BaseResponse<SettingResponse> getPolicies(@CurrentUserId Long userId) {
        return BaseResponse.ok(policyService.getAllActivePolicies(userId));
    }
}
