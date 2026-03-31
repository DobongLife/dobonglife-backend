package com.umust.dobonglife.common.security.principal;

import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Slf4j
@Builder
@Getter
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails, OAuth2User {

    private final Long userId;
    private final String userName;
    private final String password;
    private final Role role;
    private final Provider provider;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return userName; }

    @Override
    public Map<String, Object> getAttributes() { return Map.of(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public String getName() { return ""; }

    @Override
    public <A> A getAttribute(String name) { return OAuth2User.super.getAttribute(name); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> merged = new LinkedHashSet<>();
        if (this.authorities != null) merged.addAll(this.authorities);
        if (this.role != null) {
            String authority = this.role.toAuthority().toString();
            merged.add(new SimpleGrantedAuthority(authority));
        }
        return Collections.unmodifiableSet(merged);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPrincipal that)) return false;
        return Objects.equals(this.userId, that.userId);
    }

    @Override
    public int hashCode() { return Objects.hash(this.userId); }
}
