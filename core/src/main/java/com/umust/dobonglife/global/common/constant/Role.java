package com.umust.dobonglife.global.common.constant;

import com.umust.dobonglife.global.error.CommonErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public enum Role {

    MEMBER("MEMBER"),
    MANAGER("MANAGER"),
    ADMIN("ADMIN");

    Role(String value) {
        this.value = value;
        this.role = PREFIX + value;
    }

    public static final String PREFIX = "ROLE_";
    private final String value;
    private final String role;

    public static Role fromRole(String roleString) {
        if (roleString != null && roleString.startsWith(PREFIX)) {
            String roleValue = roleString.substring(PREFIX.length());
            for (Role role : Role.values()) {
                if (role.value.equalsIgnoreCase(roleValue)) {
                    return role;
                }
            }
        }
        throw new IllegalArgumentException("Unknown role: " + roleString);
    }

    public GrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority(PREFIX + this.value);
    }

    public static Role fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(CommonErrorCode.BAD_REQUEST);
        }

        for (Role role : Role.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }

        throw new BusinessException(CommonErrorCode.BAD_REQUEST);
    }
}
