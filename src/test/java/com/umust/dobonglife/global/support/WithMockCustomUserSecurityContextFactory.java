package com.umust.dobonglife.global.support;




import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.auth.domain.entity.UserPrincipal;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "1234";

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        String encodedPassword = passwordEncoder.encode(TEST_PASSWORD);

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId(1L)
                .userName(TEST_EMAIL)
                .password(encodedPassword)
                .role(Role.MEMBER)
                .provider(Provider.LOCAL)
                .authorities(List.of(new SimpleGrantedAuthority(Role.MEMBER.getRole())))
                .build();

        Authentication auth =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
