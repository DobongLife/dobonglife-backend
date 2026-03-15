package com.umust.dobonglife.global.auth.security.filter;

import com.umust.dobonglife.domain.auth.application.port.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.domain.auth.domain.AuthenticatedUser;
import com.umust.dobonglife.domain.auth.domain.UserPrincipal;
import com.umust.dobonglife.domain.auth.exception.CustomAuthenticationException;
import com.umust.dobonglife.global.auth.security.handler.CustomAuthenticationEntryPoint;
import com.umust.dobonglife.global.error.DomainErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public final static List<String> PASS_URIS = Arrays.asList(
            "/api/auth/login/**",
            "/api/auth/logout",
            "/login/oauth2/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/docs/**",
            "/api/test/**",
            "/api/users/signup",
            "/api/home/**",
            "/api/users/mail/send",
            "/api/users/mail/check",
            "/api/users/password"
    );

    private static final AntPathMatcher ANT = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            if (isPassUri(request.getRequestURI())) {
                log.info("JWT Filter Passed (pass uri) : {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            String accessToken = authenticateAccessTokenUseCase.extractAccessToken(request)
                    .orElseThrow(() -> new CustomAuthenticationException(DomainErrorCode.SECURITY_UNAUTHORIZED));

            AuthenticatedUser user = authenticateAccessTokenUseCase.authenticate(accessToken);

            List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority(user.role().toAuthority().getAuthority())
            );

            UserPrincipal principal = UserPrincipal.builder()
                    .userId(user.userId())
                    .userName(user.userName())
                    .role(user.role())
                    .provider(user.provider())
                    .authorities(authorities)
                    .build();

            Authentication authToken;
            if ("local".equals(user.provider().getValue())) {
                authToken = new UsernamePasswordAuthenticationToken(principal, null, authorities);
            } else {
                authToken = new OAuth2AuthenticationToken(principal, authorities, user.provider().getValue());
            }
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.info("JWT Filter Success : {}", request.getRequestURI());
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            customAuthenticationEntryPoint.commence(request, response, e);
        }
    }

    private boolean isPassUri(String uri) {
        return PASS_URIS.stream().anyMatch(pattern -> ANT.match(pattern, uri));
    }
}
