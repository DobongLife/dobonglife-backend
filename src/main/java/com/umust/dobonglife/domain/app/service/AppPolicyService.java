package com.umust.dobonglife.domain.app.service;

import com.umust.dobonglife.domain.app.controller.dto.response.PolicyResponse;
import com.umust.dobonglife.domain.app.controller.dto.response.SettingResponse;
import com.umust.dobonglife.domain.app.domain.repository.AppPolicyRepository;
import com.umust.dobonglife.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppPolicyService {
    private final AppPolicyRepository policyRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public SettingResponse getAllActivePolicies(Long userId) {
        List<PolicyResponse> policies = policyRepository.findAllByIsActiveTrue().stream()
                .map(p -> new PolicyResponse(p.getPolicyType(), p.getVersion(), p.getContent()))
                .collect(Collectors.toList());
        boolean receivedAlarmUser = userService.isReceivedAlarmUser(userId);
        return SettingResponse.from(receivedAlarmUser, policies);
    }
}
