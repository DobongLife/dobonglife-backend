package com.umust.dobonglife.domain.app.controller;

import com.umust.dobonglife.domain.app.controller.dto.response.PolicyResponse;
import com.umust.dobonglife.domain.app.controller.dto.response.SettingResponse;
import com.umust.dobonglife.domain.app.service.AppPolicyService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class AppPolicyController {
    private final AppPolicyService policyService;

    @GetMapping
    public ResponseEntity<SettingResponse> getPolicies(@CurrentUserId Long userId) {
        return ResponseEntity.ok(policyService.getAllActivePolicies(userId));
    }
}
