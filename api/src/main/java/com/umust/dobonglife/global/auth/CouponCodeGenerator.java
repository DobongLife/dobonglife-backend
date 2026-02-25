package com.umust.dobonglife.global.auth;

import java.util.UUID;

public class CouponCodeGenerator {
    public static String generate() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
