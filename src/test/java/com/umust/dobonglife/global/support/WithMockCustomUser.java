package com.umust.dobonglife.global.support;

import com.umust.dobonglife.domain.user.model.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    String email() default "test@naver.com";
    String password() default  "1234";
    String username() default "test";

    Role role() default Role.MEMBER;
}
