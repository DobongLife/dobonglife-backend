package com.umust.dobonglife.auth.config;

import com.umust.dobonglife.auth.security.filter.JwtExceptionHandlerFilter;
import com.umust.dobonglife.auth.security.handler.CustomAccessDeniedHandler;
import com.umust.dobonglife.auth.security.handler.CustomAuthenticationEntryPoint;
import com.umust.dobonglife.auth.security.handler.CustomSessionExpiredStrategy;
import com.umust.dobonglife.auth.security.filter.CustomLoginFilter;
import com.umust.dobonglife.auth.security.filter.JwtAuthenticationFilter;
import com.umust.dobonglife.auth.security.filter.InternalApiKeyFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtExceptionHandlerFilter jwtExceptionHandlerFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomSessionExpiredStrategy customSessionExpiredStrategy;

    @Value("${internal.api-key}")
    private String internalApiKey;

    /**
     * 1) Swagger 전용 체인: 동시 로그인(세션 max 1) 정책 제외
     * - swagger 관련 URI만 매칭
     * - 필요하면 permitAll로 열어둠
     */
    @Bean
    @Order(1)
    public SecurityFilterChain swaggerFilterChain(HttpSecurity http,
                                                  CorsConfigurationSource corsConfigurationSource) throws Exception {

        http
                .securityMatcher(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/docs/**",
                        "/api/test/**"
                )
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .httpBasic(b -> b.disable())
                .formLogin(fl -> fl.disable())
                // swagger 쪽은 세션을 아예 안 쓰게 하고 싶으면 아래 한 줄 추천
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    /**
     * 2) Internal API 체인: 서버 내부 호출 전용 (인증 불필요)
     */
    @Bean
    @Order(2)
    public SecurityFilterChain internalFilterChain(HttpSecurity http,
                                                    CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .securityMatcher("/internal/**")
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .httpBasic(b -> b.disable())
                .formLogin(fl -> fl.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new InternalApiKeyFilter(internalApiKey), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    /**
     * 3) API 체인: 기존 정책 유지 (동시 로그인 max 1 + expiredStrategy)
     */
    @Bean
    @Order(3)
    public SecurityFilterChain apiFilterChain(HttpSecurity http,
                                              CustomLoginFilter customLoginFilter,
                                              CorsConfigurationSource corsConfigurationSource) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .httpBasic(b -> b.disable())
                .formLogin(fl -> fl.disable())
                .sessionManagement(sm -> sm
                        .sessionFixation(sf -> sf.changeSessionId())
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .expiredSessionStrategy(customSessionExpiredStrategy)
                        .sessionRegistry(sessionRegistry())
                );

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/error",
                                "/favicon.ico",
                                "/actuator/health",
                                "/api/auth/login/**",
                                "/api/auth/reissue",
                                "/login/oauth2/**",
                                "/api/users/signup",
                                "/api/users/mail/send",
                                "/api/users/mail/check",
                                "/api/users/password",
                                "/api/home/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        http
                .addFilterBefore(jwtExceptionHandlerFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(customLoginFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }
}
