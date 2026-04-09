package com.umust.dobonglife.test.support;

import com.umust.dobonglife.common.security.principal.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId(annotation.userId())
                .userName(annotation.username())
                .password("encoded-test-password")
                .role(annotation.role())
                .provider(annotation.provider())
                .authorities(List.of(new SimpleGrantedAuthority(annotation.role().getRole())))
                .build();

        Authentication auth =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
