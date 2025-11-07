package com.umust.dobonglife.domain.auth.presentation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jooeon.mybeauty.domain.auth.model.dto.JwtInfo;
import me.jooeon.mybeauty.domain.auth.utils.CookieUtil;
import me.jooeon.mybeauty.global.common.model.dto.BaseResponse;
import me.jooeon.mybeauty.global.common.model.enums.BaseResponseStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Controller
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class TestLoginController {

    private final CookieUtil cookieUtil;

    @GetMapping("/kakaoPage")
    public String kakaoLoginPage() {
        return "kakao-login"; // resources/templates/kakao-login.html
    }

    @GetMapping("/googlePage")
    public String googleLoginPage() {
        return "google-login";
    }

    @GetMapping("/successPage")
    public String successPage() {
        return "success"; // → templates/success.html
    }
}