package com.umust.dobonglife.domain.auth.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(false);

        return cookie;
    }

    public String getCookieValue(HttpServletRequest request, String key) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(key)) {
                    return cookie.getValue();
                }
            }
        }
        return null; // 쿠키가 없으면 null 반환
    }
}
