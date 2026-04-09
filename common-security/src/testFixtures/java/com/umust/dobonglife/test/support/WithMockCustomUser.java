package com.umust.dobonglife.test.support;

import com.umust.dobonglife.global.common.constant.Provider;
import com.umust.dobonglife.global.common.constant.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    long userId() default 1L;
    String username() default "test@example.com";
    Role role() default Role.MEMBER;
    Provider provider() default Provider.LOCAL;
}
