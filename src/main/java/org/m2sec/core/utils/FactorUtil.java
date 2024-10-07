package org.m2sec.core.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author: outlaws-bai
 * @date: 2024/7/20 20:05
 * @description:
 */

public class FactorUtil {

    public static String randomString(int length) {
        @SuppressWarnings("SpellCheckingInspection") String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
        return random(length, CHARACTERS);
    }

    public static Integer randomInteger(int length) {
        String CHARACTERS = "0123456789";
        return Integer.valueOf(random(length, CHARACTERS));
    }

    private static String random(int length, String CHARACTERS) {
        SecureRandom RANDOM = new SecureRandom();
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static String currentDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    public static Long currentTime() {
        return System.currentTimeMillis();
    }
}
