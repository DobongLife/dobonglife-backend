package com.umust.dobonglife.domain.auth.infrastructure.security.filter;

import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.domain.auth.domain.UserPrincipal;
import com.umust.dobonglife.domain.auth.exception.CustomAuthenticationException;
import com.umust.dobonglife.domain.auth.exception.CustomJwtException;
import com.umust.dobonglife.domain.auth.infrastructure.security.handler.CustomAuthenticationEntryPoint;
import com.umust.dobonglife.domain.auth.application.service.JwtService;
import com.umust.dobonglife.domain.auth.infrastructure.jwt.JwtTokenProvider;
import com.umust.dobonglife.global.common.constant.Role;
import com.umust.dobonglife.global.error.ErrorCode;
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
    private final JwtTokenProvider jwtUtil;
    private final JwtService jwtService;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            if (isPassUri(request.getRequestURI())) {
                log.info("JWT Filter Passed (pass uri) : {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            log.info("Request URI: {}", request.getRequestURI());
            String accessToken = jwtUtil.extractAccessToken(request)
                    .orElseThrow(() -> new CustomAuthenticationException(ErrorCode.SECURITY_UNAUTHORIZED));

            jwtUtil.validateToken(accessToken);

            if (!"access".equals(jwtUtil.getTokenType(accessToken))) {
                throw new CustomJwtException(ErrorCode.INVALID_TOKEN_TYPE);
            }

            jwtService.checkLogout(accessToken);

            List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(jwtUtil.getRole(accessToken)));
            log.info("Granted Authorities : {}", authorities);
            UserPrincipal principal = UserPrincipal.builder()
                    .userId(jwtUtil.getUserId(accessToken))
                    .userName(jwtUtil.getName(accessToken))
                    .role(Role.fromRole(jwtUtil.getRole(accessToken)))
                    .provider(Provider.fromProvider(jwtUtil.getProvider(accessToken)))
                    .authorities(authorities)
                    .build();
            log.info("UserPrincipal.userId: {}", principal.getUserId());
            log.info("UserPrincipal.userName: {}", principal.getUsername());
            log.info("UserPrincipal.provider: {}", principal.getProvider());
            log.info("UserPrincipal.role: {}", principal.getAuthorities().stream().findFirst().get().toString());

            Authentication authToken = null;
            if ("local".equals(principal.getProvider().getValue())) {
                authToken = new UsernamePasswordAuthenticationToken(principal, null, authorities);
            }
            else {
                authToken = new OAuth2AuthenticationToken(principal, authorities, principal.getProvider().getValue());
            }
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.info("Authentication set in SecurityContext: {}", SecurityContextHolder.getContext().getAuthentication());
            log.info("Authorities in SecurityContext: {}", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
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
