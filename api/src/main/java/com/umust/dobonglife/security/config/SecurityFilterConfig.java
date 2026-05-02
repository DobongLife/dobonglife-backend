package com.umust.dobonglife.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.security.filter.CustomLoginFilter;
import com.umust.dobonglife.security.handler.CustomAuthenticationSuccessHandler;
import com.umust.dobonglife.security.handler.CustomJsonAuthenticationFailureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
@RequiredArgsConstructor
public class SecurityFilterConfig {

    private final ObjectMapper objectMapper;
    private final AuthenticationConfiguration configuration;
    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomJsonAuthenticationFailureHandler failureHandler;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CustomLoginFilter customLoginFilter() throws Exception {
        return new CustomLoginFilter(
                authenticationManager(),
                objectMapper,
                successHandler,
                failureHandler
        );
    }
}
