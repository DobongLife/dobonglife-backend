package com.umust.dobonglife.domain.auth.filter;

import jakarta.security.auth.message.AuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jooeon.mybeauty.domain.auth.application.JwtService;
import me.jooeon.mybeauty.domain.auth.utils.JwtUtil;
import me.jooeon.mybeauty.domain.auth.model.AuthDto;
import me.jooeon.mybeauty.domain.auth.model.dto.CustomOAuth2User;
import me.jooeon.mybeauty.domain.member.model.Role;
import me.jooeon.mybeauty.global.common.exception.exception.auth.JwtException;
import me.jooeon.mybeauty.global.common.exception.exception.auth.LogoutException;
import me.jooeon.mybeauty.global.common.model.enums.BaseResponseStatus;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.Cookie;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtService jwtService;

    // 인증을 안해도 되니 토큰이 필요없는 URL들 (에러: 로그인이 필요합니다)
    public final static List<String> PASS_URIS = Arrays.asList(
//            "/login",
//            "/login",
//            "/api/login/success",
//            "/app/api/image/test",
//            "/api/login/kakao",
//            "/api/login/google",
//            "/static/**",
//            "/api/login/googlePage",
//            "/api/login/kakaoPage",
//            "/api/login/successPage"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(isPassUris(request.getRequestURI())) {
            log.info("JWT Filter Passed (pass uri) : {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Request URI: {}", request.getRequestURI()); // 요청 URI 로깅
        String accessToken = jwtUtil.resolveAccessToken(request);

        // 엑세스 토큰이 없으면 Authentication도 없음 -> EntryPoint (401)
        if(accessToken == null) {
            log.info("JWT Filter Pass (accessToken is null) : {}", request.getRequestURI());
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 유효성 검사
        jwtUtil.validateToken(accessToken);

        // 토큰 타입 검사
        if(!"access".equals(jwtUtil.getTokenType(accessToken))) {
            throw new JwtException(BaseResponseStatus.INVALID_TOKEN_TYPE);
        }

        jwtService.checkLogout(request);

        // 권한 리스트 생성
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(jwtUtil.getRole(accessToken)));
        log.info("Granted Authorities : {}", authorities);

        //todo
        // CustomOAuth2User 생성
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(AuthDto.builder()
                .memberId(jwtUtil.getMemberId(accessToken))
                .providerId(jwtUtil.getProviderId(accessToken))
                .role(Role.fromRole(jwtUtil.getRole(accessToken)))
                .build(), authorities);
//                .build());

        log.info("CustomOAuth2User created: {}", customOAuth2User); // 생성된 사용자 정보 로깅
        log.info("CustomOAuth2User.providerId: {}", customOAuth2User.getProviderId());
        log.info("CustomOAuth2User.role: {}", customOAuth2User.getAuthorities().stream().findFirst().get().toString());
        log.info("CustomOAuth2User.memberId: {}", customOAuth2User.getMemberId());

        Authentication authToken = new OAuth2AuthenticationToken(customOAuth2User, customOAuth2User.getAuthorities(), customOAuth2User.getProviderId());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.info("Authentication set in SecurityContext: {}", SecurityContextHolder.getContext().getAuthentication()); // SecurityContext 설정 확인 로깅
        log.info("Authorities in SecurityContext: {}", authToken.getAuthorities());

        log.info("JWT Filter Success : {}", request.getRequestURI());
        filterChain.doFilter(request, response);
    }

    private boolean isPassUris(String uri) {
        return PASS_URIS.contains(uri);
    }
}
