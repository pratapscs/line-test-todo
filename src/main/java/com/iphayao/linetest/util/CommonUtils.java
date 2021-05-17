package com.iphayao.linetest.util;


import java.security.SecureRandom;
import java.util.Base64;

public class CommonUtils {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String getToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

    }
}
