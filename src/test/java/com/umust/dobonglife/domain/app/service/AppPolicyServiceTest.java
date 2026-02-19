package com.umust.dobonglife.domain.app.service;

import com.umust.dobonglife.domain.app.controller.dto.response.SettingResponse;
import com.umust.dobonglife.domain.app.domain.constant.PolicyType;
import com.umust.dobonglife.domain.app.domain.entity.AppPolicy;
import com.umust.dobonglife.domain.app.domain.repository.AppPolicyRepository;
import com.umust.dobonglife.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AppPolicyServiceTest {

    @Mock
    AppPolicyRepository policyRepository;

    @Mock
    UserService userService;

    @InjectMocks
    AppPolicyService appPolicyService;

    @Nested
    @DisplayName("getAllActivePolicies - 활성 정책 조회")
    class GetAllActivePolicies {

        @Test
        @DisplayName("정책과 알림 설정 반환")
        void 정책과_알림_설정_반환() {
            // given
            Long userId = 1L;
            AppPolicy policy = AppPolicy.builder()
                    .policyType(PolicyType.TERMS)
                    .version("1.0")
                    .content("이용약관 내용")
                    .isActive(true)
                    .build();

            given(policyRepository.findAllByIsActiveTrue()).willReturn(List.of(policy));
            given(userService.isReceivedAlarmUser(userId)).willReturn(true);

            // when
            SettingResponse result = appPolicyService.getAllActivePolicies(userId);

            // then
            assertThat(result.isReceivedAlarm()).isTrue();
            assertThat(result.policies()).hasSize(1);
            assertThat(result.policies().get(0).getType()).isEqualTo(PolicyType.TERMS);
        }

        @Test
        @DisplayName("빈 정책 목록")
        void 빈_정책_목록() {
            // given
            Long userId = 1L;

            given(policyRepository.findAllByIsActiveTrue()).willReturn(List.of());
            given(userService.isReceivedAlarmUser(userId)).willReturn(false);

            // when
            SettingResponse result = appPolicyService.getAllActivePolicies(userId);

            // then
            assertThat(result.isReceivedAlarm()).isFalse();
            assertThat(result.policies()).isEmpty();
        }
    }
}
