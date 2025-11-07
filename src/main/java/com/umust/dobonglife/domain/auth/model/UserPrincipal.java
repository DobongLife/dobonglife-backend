package com.umust.dobonglife.domain.auth.model;

import com.umust.dobonglife.domain.user.model.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Slf4j
@Getter
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails, OAuth2User {

    private final Long userId;
    private final String username;
    private final String password;
    private final String providerId;
    private final Role role;
    private final Collection<? extends GrantedAuthority> authorities;

    /** UserDetails 구현 */
    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 중복 제거 + 순서 보존
        Set<GrantedAuthority> merged = new LinkedHashSet<>();

        // 1) 기존에 주입/계산된 authorities가 있다면 먼저 추가
        if (this.authorities != null) {
            merged.addAll(this.authorities);
        }

        // 2) memberPrincipal의 Role 기반 권한 추가
        if (this.role != null) {
            // 예: ROLE_USER / ROLE_ADMIN 형태 보장
            String authority = this.role.toAuthority().toString();
            merged.add(new SimpleGrantedAuthority(authority));
            log.info("[CustomOAuth2User] merged authority = {}", authority);
        }

        // 불변으로 감싸서 외부 변경 방지
        return Collections.unmodifiableSet(merged);
    }
}

