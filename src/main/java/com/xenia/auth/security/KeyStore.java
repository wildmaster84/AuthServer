package com.xenia.auth.security;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class KeyStore {
    private static final Map<String, String> lookup = new HashMap<>();
    private static final SecureRandom random = new SecureRandom();
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static String generateKey(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public static String store(String value) {
        String key;
        do {
            key = generateKey(16); // fixed 16-char key
        } while (lookup.containsKey(key));
        lookup.put(key, value);
        return key;
    }

    public static String retrieve(String key) {
        return lookup.get(key);
    }

    public static void remove(String key) {
        lookup.remove(key);
    }
}
