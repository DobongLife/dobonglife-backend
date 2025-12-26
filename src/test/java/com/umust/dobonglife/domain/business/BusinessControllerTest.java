package com.umust.dobonglife.domain.business;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.domain.auth.controller.dto.request.RefreshTokenRequest;
import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.auth.service.CustomUserDetailsService;
import com.umust.dobonglife.domain.auth.utils.JwtUtil;
import com.umust.dobonglife.domain.business.controller.dto.request.BusinessRequest;
import com.umust.dobonglife.domain.business.domain.repository.BusinessRepository;
import com.umust.dobonglife.domain.business.service.BusinessService;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.common.webclient.service.WebClientService;
import com.umust.dobonglife.global.support.WithMockCustomUser;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class BusinessControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BusinessService businessService;

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    UserRepository userRepository;

    @MockitoBean
    JwtUtil jwtUtil;

    @MockitoBean
    private WebClientService webClientService;

    @Test
    @DisplayName("사업자 등록 성공")
    @WithMockCustomUser
    void registerBusiness_success() throws Exception {

        // given

        User user = User.builder()
                .name("test")
                .email("test@example.com")
                .password("1234")
                .role(Role.ADMIN)
                .provider(Provider.LOCAL)
                .build();
        User savedUser = userRepository.save(user);


        String TEST_ACCESS_TOKEN = "access-token";

        BusinessRequest request = BusinessRequest.builder()
                .businessName("도봉 카페")
                .businessAddress("서울 도봉구 어딘가 123")
                .introduction("소개글")
                .phoneNumber("010-1234-5678")
                .managerName("홍길동")
                .email("biz@example.com")
                .link("https://example.com")
                .operatingHour("09:00~18:00")
                .businessCategory("CAFE")
                .businessNumber("219-87-01322")
                .businessService(List.of("PARKING", "WIFI"))
                .build();


        String json = objectMapper.writeValueAsString(request);

        Map<String, Object> response = Map.of(
                "status_code", "OK",
                "data", List.of(Map.of("b_stt_cd", "01"))
        );

        given(webClientService.getCompanyStatus(anyString()))
                .willReturn(response);
        "business-register-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),

                // ✅ Swagger(OpenAPI) 메타데이터\n" +
                //
        when(jwtUtil.extractAccessToken(any(HttpServletRequest.class)))
                .thenReturn(Optional.of(TEST_ACCESS_TOKEN));

        // when & then
        mockMvc.perform(post("/api/business")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(document(                                        "                        fieldWithPath(\"businessName\").description(\"사업체 이름\"),
                        resource(ResourceSnippetParameters.builder()
                                .tag("사업자 API")
                                .summary("사업자 등록")
                                .description("로그인한 사용자가 사업자 정보를 등록합니다.")
                                .requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION)
                                                .description("Bearer {accessToken}")
                                )
                                .requestFields(
                                        fieldWithPath("businessName").description("사업체 이름"),
                                        fieldWithPath("businessAddress").description("사업장 주소"),
                                        fieldWithPath("introduction").description("소개글"),
                                        fieldWithPath("phoneNumber").description("전화번호"),
                                        fieldWithPath("managerName").description("담당자 이름"),
                                        fieldWithPath("email").description("이메일"),
                                        fieldWithPath("link").description("웹사이트 링크"),
                                        fieldWithPath("operatingHour").description("운영 시간"),
                                        fieldWithPath("businessCategory").description("사업자 카테고리"),
                                        fieldWithPath("businessNumber").description("사업자 등록번호"),
                                        fieldWithPath("businessService").description("제공 서비스/편의시설 목록")
                                )
                                .responseFields(
                                        fieldWithPath("status").description("응답 상태"),
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("data").description("응답 데이터 (없음)")
                                )
                                .build()),

                        // ✅ REST Docs 스니펫
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("Bearer {accessToken}")
                        ),
                        requestFields(
                                fieldWithPath("managerName").description("담당자 이름"),
                                fieldWithPath("businessAddress").description("사업장 주소"),
                                fieldWithPath("introduction").description("소개글"),
                                fieldWithPath("phoneNumber").description("전화번호"),
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("link").description("웹사이트 링크"),
                                fieldWithPath("operatingHour").description("운영 시간"),
                                fieldWithPath("businessCategory").description("사업자 카테고리"),
                                fieldWithPath("businessNumber").description("사업자 등록번호"),
                                fieldWithPath("businessService").description("제공 서비스/편의시설 목록")
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("status").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터 (없으면 null)").optional()
                        )
                ));

        // 서비스 호출 검증
        verify(businessService).registerBusiness(any(BusinessRequest.class), anyLong());
    }
}
