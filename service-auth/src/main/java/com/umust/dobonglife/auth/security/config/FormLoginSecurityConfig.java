package com.umust.dobonglife.auth.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.auth.security.filter.CustomLoginFilter;
import com.umust.dobonglife.auth.security.handler.FormLoginFailureHandler;
import com.umust.dobonglife.auth.security.handler.FormLoginSuccessHandler;
import com.umust.dobonglife.auth.security.provider.LocalAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class FormLoginSecurityConfig {

    private final ObjectMapper objectMapper;
    private final FormLoginSuccessHandler successHandler;
    private final FormLoginFailureHandler failureHandler;
    private final LocalAuthenticationProvider localAuthenticationProvider;

    @Bean
    public AuthenticationManager formLoginAuthenticationManager() {
        return new ProviderManager(localAuthenticationProvider);
    }

    @Bean
    public CustomLoginFilter customLoginFilter() {
        return new CustomLoginFilter(
                formLoginAuthenticationManager(),
                objectMapper,
                successHandler,
                failureHandler
        );
    }

    @Bean
    @Order(0)
    public SecurityFilterChain formLoginFilterChain(HttpSecurity http,
                                                    CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .securityMatcher("/api/auth/login")
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .httpBasic(b -> b.disable())
                .formLogin(fl -> fl.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterAt(customLoginFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
