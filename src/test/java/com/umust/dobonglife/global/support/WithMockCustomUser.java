package com.umust.dobonglife.global.support;

import com.umust.dobonglife.domain.auth.model.Provider;
import com.umust.dobonglife.domain.user.model.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    String username() default "test@example.com";
    String password() default  "1234";
    Role role() default Role.MEMBER;
    Provider provider() default Provider.LOCAL;
}
