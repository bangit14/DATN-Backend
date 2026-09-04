package com.backend.applyingservice.core.utils;

import java.security.SecureRandom;

public final class StringsUtils {

    private StringsUtils() {}

    public static boolean isEquals(final String s1, final String s2) {
        return isEquals(s1, s2, false);
    }

    public static boolean isEquals(String s1, String s2, boolean ignoreCase) {
        if (s1 == s2) return true;
        if (s1 == null || s2 == null) return false;
        return ignoreCase ? s1.equalsIgnoreCase(s2) : s1.equals(s2);
    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
}