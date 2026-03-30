package com.umust.dobonglife.security.filter;

import com.umust.dobonglife.global.port.auth.in.AuthenticateAccessTokenUseCase;
import com.umust.dobonglife.global.port.auth.dto.AuthenticatedUser;
import com.umust.dobonglife.security.extractor.TokenExtractor;
import com.umust.dobonglife.security.principal.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenExtractor tokenExtractor;
    private final AuthenticateAccessTokenUseCase authenticateAccessTokenUseCase;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Optional<String> accessTokenOptional = tokenExtractor.extractAccessTokenOptional(request);

        if (accessTokenOptional.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthenticatedUser user = authenticateAccessTokenUseCase.authenticate(accessTokenOptional.get());

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

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, authorities)
        );

        log.info("JWT Filter Success : {}", request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}
