package com.umust.dobonglife.content.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.common.security.extractor.TokenExtractor;
import com.umust.dobonglife.content.controller.ImageController.PresignedUrlRequest;
import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.infra.s3.S3Utils;
import com.umust.dobonglife.infra.s3.S3Utils.PresignedUrlResult;
import com.umust.dobonglife.test.support.TestWebMvcConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ImageController.class, excludeAutoConfiguration = {
        OAuth2ClientAutoConfiguration.class,
        OAuth2ClientWebSecurityAutoConfiguration.class,
        SecurityAutoConfiguration.class})
@Import(TestWebMvcConfig.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ActiveProfiles("test")
class ImageControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockitoBean S3Utils s3Utils;
    @MockitoBean AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;
    @MockitoBean TokenExtractor tokenExtractor;

    @Test
    @DisplayName("Presigned URL 발급 - 성공")
    void getPresignedUrls_success() throws Exception {
        PresignedUrlRequest request = new PresignedUrlRequest(List.of(".jpg", ".png"));

        given(s3Utils.generatePresignedUrls(anyList()))
                .willReturn(List.of(
                        new PresignedUrlResult(
                                "https://s3.amazonaws.com/bucket/images/uuid1.jpg?presigned",
                                "https://s3.amazonaws.com/bucket/images/uuid1.jpg"),
                        new PresignedUrlResult(
                                "https://s3.amazonaws.com/bucket/images/uuid2.png?presigned",
                                "https://s3.amazonaws.com/bucket/images/uuid2.png")
                ));

        mockMvc.perform(post("/api/image/presigned-url")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].presignedUrl").exists())
                .andExpect(jsonPath("$.data[0].imageUrl").exists())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andDo(print())
                .andDo(document("image-presigned-url",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .tag("이미지 API").summary("Presigned URL 발급")
                                .description("S3 이미지 업로드용 Presigned URL을 발급합니다.")
                                .requestFields(
                                        fieldWithPath("extensions").type(JsonFieldType.ARRAY).description("이미지 확장자 목록 (.jpg, .png 등)"))
                                .responseFields(
                                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data[].presignedUrl").type(JsonFieldType.STRING).description("S3 업로드용 Presigned URL"),
                                        fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING).description("업로드 후 이미지 접근 URL"))
                                .build())
                ));

        verify(s3Utils).generatePresignedUrls(anyList());
    }
}
