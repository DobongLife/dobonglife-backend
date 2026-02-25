package com.umust.dobonglife.global.web;

<<<<<<< Updated upstream
=======
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
>>>>>>> Stashed changes
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

<<<<<<< Updated upstream
=======
    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @PostConstruct
    void validateOrigins() {
        if (allowedOrigins.contains("*")) {
            throw new IllegalStateException(
                    "cors.allowed-origins에 '*'를 사용할 수 없습니다. "
                    + "allowCredentials(true)와 함께 사용하면 CORS 정책 충돌이 발생합니다. "
                    + "명시적인 origin을 지정해 주세요.");
        }
    }

>>>>>>> Stashed changes
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "https://api.dobonglife.co.kr",
                "http://localhost:8080",
                "http://localhost:3000"
        ));

        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        configuration.setAllowedHeaders(Arrays.asList("*"));

        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Authorization-refresh"
        ));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
