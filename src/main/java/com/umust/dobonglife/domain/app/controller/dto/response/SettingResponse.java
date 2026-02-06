package com.umust.dobonglife.domain.app.controller.dto.response;

import java.util.List;

public record SettingResponse(
        boolean isReceivedAlarm,
        List<PolicyResponse> policies
) {
    public static SettingResponse from(boolean receivedAlarmUser, List<PolicyResponse> policies) {
        return new SettingResponse(receivedAlarmUser, policies);
    }
}
