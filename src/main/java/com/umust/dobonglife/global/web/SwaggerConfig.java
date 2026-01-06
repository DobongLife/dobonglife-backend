package com.umust.dobonglife.global.web;

import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    static {
        org.springdoc.core.utils.SpringDocUtils.getConfig().addAnnotationsToIgnore(
                CurrentUserId.class
        );
    }
    @Bean
    public OpenAPI openAPI() {
        List<Server> servers = new ArrayList<>();

        servers.add(new Server()
                .url("http://localhost:8080")
                .description("로컬 개발 서버"));

        servers.add(new Server()
                .url("https://api.dobonglife.co.kr")
                .description("운영 서버"));

        Components components = new Components()
                // ✅ Access Token (Authorization: Bearer <token>)
                .addSecuritySchemes("BearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Access Token: Authorization 헤더에 Bearer {accessToken}"))
                // ✅ Refresh Token (Authorization-refresh: Bearer <token>)
                .addSecuritySchemes("RefreshAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization-refresh")
                                .description("Refresh Token: Authorization-refresh 헤더에 Bearer {refreshToken}"));

        return new OpenAPI()
                .components(components)
                .info(apiInfo())
                .servers(servers)
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
    }


    private Info apiInfo() {
        return new Info()
                .title("도봉라이프 API (Spring Doc)")
                .description("도봉구 스토리 관광 가이드")
                .version("1.0.0");
    }
}
