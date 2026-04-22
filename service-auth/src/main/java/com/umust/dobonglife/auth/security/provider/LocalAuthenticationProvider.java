package com.umust.dobonglife.auth.security.provider;

import com.umust.dobonglife.auth.client.UserServiceClient;
import com.umust.dobonglife.auth.client.UserServiceClient.LocalUserResponse;
import com.umust.dobonglife.common.security.principal.UserPrincipal;
import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class LocalAuthenticationProvider implements AuthenticationProvider {

    private final UserServiceClient userServiceClient;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String rawPassword = (String) authentication.getCredentials();

        LocalUserResponse user = userServiceClient.findLocalUser(email)
                .orElseThrow(() -> new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(rawPassword, user.password())) {
            throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        Role role = Role.valueOf(user.role());
        Provider provider = Provider.valueOf(user.provider());

        UserPrincipal principal = UserPrincipal.builder()
                .userId(user.userId())
                .userName(user.email())
                .password(user.password())
                .role(role)
                .provider(provider)
                .authorities(Collections.singleton(role.toAuthority()))
                .build();

        UsernamePasswordAuthenticationToken result =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
