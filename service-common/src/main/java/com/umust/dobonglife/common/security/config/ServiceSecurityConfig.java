package com.umust.dobonglife.common.security.config;

import com.umust.dobonglife.common.security.filter.JwtAuthenticationFilter;
import com.umust.dobonglife.common.security.filter.JwtExceptionHandlerFilter;
import com.umust.dobonglife.common.security.handler.CustomAccessDeniedHandler;
import com.umust.dobonglife.common.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * 각 MSA 서비스에서 공유하는 기본 Security 설정.
 * 서비스별 추가 설정이 필요하면 별도 @Configuration으로 오버라이드.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ServiceSecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtExceptionHandlerFilter jwtExceptionHandlerFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    @Order(1)
    public SecurityFilterChain internalFilterChain(HttpSecurity http,
                                                    CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .securityMatcher("/internal/**", "/actuator/**")
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .httpBasic(b -> b.disable())
                .formLogin(fl -> fl.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http,
                                              CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .httpBasic(b -> b.disable())
                .formLogin(fl -> fl.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/", "/error", "/favicon.ico", "/actuator/health",
                        "/api/auth/login/**", "/api/auth/reissue",
                        "/api/users/signup", "/api/users/mail/send",
                        "/api/users/mail/check", "/api/users/password",
                        "/api/home/**",
                        "/swagger-ui/**", "/v3/api-docs/**", "/docs/**"
                ).permitAll()
                .anyRequest().authenticated()
        );

        http
                .addFilterBefore(jwtExceptionHandlerFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        );

        return http.build();
    }
}
