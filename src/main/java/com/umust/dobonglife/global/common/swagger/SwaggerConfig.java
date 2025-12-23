package com.umust.dobonglife.global.common.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        List<Server> servers = new ArrayList<>();

        servers.add(new Server()
                .url("http://localhost:8080")
                .description("로컬 개발 서버"));

        servers.add(new Server()
                .url("https://api.dobonglife.co.kr")
                .description("운영 서버"));

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("BearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
                .info(apiInfo())
                .servers(servers);
    }


    private Info apiInfo() {
        return new Info()
                .title("도봉라이프 API (Spring Doc)")
                .description("도봉구 스토리 관광 가이드")
                .version("1.0.0");
    }
}
