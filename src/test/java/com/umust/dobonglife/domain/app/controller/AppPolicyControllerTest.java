package com.umust.dobonglife.domain.app.controller;

import com.umust.dobonglife.domain.app.controller.dto.response.PolicyResponse;
import com.umust.dobonglife.domain.app.controller.dto.response.SettingResponse;
import com.umust.dobonglife.domain.app.domain.constant.PolicyType;
import com.umust.dobonglife.domain.app.service.AppPolicyService;
import com.umust.dobonglife.global.external.firebase.FirebaseConfig;
import com.umust.dobonglife.global.support.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import com.epages.restdocs.apispec.ResourceSnippetParameters;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class AppPolicyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AppPolicyService appPolicyService;

    @MockitoBean
    FirebaseConfig firebaseConfig;

    @MockitoBean
    JavaMailSender javaMailSender;

    @Nested
    @DisplayName("GET /api/policies")
    class GetPolicies {

        @Test
        @DisplayName("정책 목록 조회 성공")
        @WithMockCustomUser
        void 정책_목록_조회_성공() throws Exception {
            List<PolicyResponse> policies = List.of(
                    new PolicyResponse(PolicyType.TERMS, "1.0", "이용약관 내용입니다."),
                    new PolicyResponse(PolicyType.PRIVACY, "1.0", "개인정보처리방침 내용입니다."),
                    new PolicyResponse(PolicyType.LOCATION, "1.0", "위치기반서비스 이용약관입니다.")
            );
            SettingResponse response = SettingResponse.from(true, policies);

            when(appPolicyService.getAllActivePolicies(eq(1L)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/policies"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                    .andExpect(jsonPath("$.data.isReceivedAlarm").value(true))
                    .andExpect(jsonPath("$.data.policies[0].type").value("TERMS"))
                    .andExpect(jsonPath("$.data.policies[0].version").value("1.0"))
                    .andExpect(jsonPath("$.data.policies[0].content").value("이용약관 내용입니다."))
                    .andExpect(jsonPath("$.data.policies[1].type").value("PRIVACY"))
                    .andExpect(jsonPath("$.data.policies[2].type").value("LOCATION"))
                    .andDo(print())
                    .andDo(document("app-policies",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("이용약관 API")
                                            .summary("앱 정책 조회")
                                            .description("앱 정책 및 알림 설정을 조회합니다.")
                                            .responseFields(
                                                    fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                                    fieldWithPath("data.isReceivedAlarm").type(JsonFieldType.BOOLEAN).description("알림 수신 여부"),
                                                    fieldWithPath("data.policies[]").type(JsonFieldType.ARRAY).description("정책 목록"),
                                                    fieldWithPath("data.policies[].type").type(JsonFieldType.STRING).description("정책 유형 (TERMS, PRIVACY, LOCATION)"),
                                                    fieldWithPath("data.policies[].version").type(JsonFieldType.STRING).description("정책 버전"),
                                                    fieldWithPath("data.policies[].content").type(JsonFieldType.STRING).description("정책 내용")
                                            )
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("알림 비활성화 사용자 정책 조회 성공")
        @WithMockCustomUser
        void 알림_비활성화_사용자_정책_조회_성공() throws Exception {
            List<PolicyResponse> policies = List.of(
                    new PolicyResponse(PolicyType.TERMS, "2.0", "개정된 이용약관 내용입니다.")
            );
            SettingResponse response = SettingResponse.from(false, policies);

            when(appPolicyService.getAllActivePolicies(eq(1L)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/policies"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                    .andExpect(jsonPath("$.data.isReceivedAlarm").value(false))
                    .andExpect(jsonPath("$.data.policies").isArray())
                    .andExpect(jsonPath("$.data.policies[0].type").value("TERMS"));
        }

        @Test
        @DisplayName("빈 정책 목록 조회 성공")
        @WithMockCustomUser
        void 빈_정책_목록_조회_성공() throws Exception {
            SettingResponse response = SettingResponse.from(true, List.of());

            when(appPolicyService.getAllActivePolicies(eq(1L)))
                    .thenReturn(response);

            mockMvc.perform(get("/api/policies"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                    .andExpect(jsonPath("$.data.isReceivedAlarm").value(true))
                    .andExpect(jsonPath("$.data.policies").isEmpty());
        }
    }
}
