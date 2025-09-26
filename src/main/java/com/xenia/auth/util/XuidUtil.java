package com.xenia.auth.util;

import java.security.SecureRandom;

public class XuidUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static boolean isOnlineXuid(String xuid) {
    	String xuidStr = xuid;
    	if (xuidStr.startsWith("0x") || xuidStr.startsWith("0X")) {
    	    xuidStr = xuidStr.substring(2);
    	}
    	long xuid_ = Long.parseUnsignedLong(xuidStr, 16);
        return ((xuid_ >> 48) & 0xFFFF) == 0x9000;
    }

    public static boolean isOfflineXuid(String xuid) {
    	String xuidStr = xuid;
    	if (xuidStr.startsWith("0x") || xuidStr.startsWith("0X")) {
    	    xuidStr = xuidStr.substring(2);
    	}
    	long xuid_ = Long.parseUnsignedLong(xuidStr, 16);
        return ((xuid_ >> 48) & 0xFFFF) == 0xE000;
    }

    public static boolean validXuid(String xuid) {
    	String xuidStr = xuid;
    	if (xuidStr.startsWith("0x") || xuidStr.startsWith("0X")) {
    	    xuidStr = xuidStr.substring(2);
    	}
        return isOnlineXuid(xuidStr) || isOfflineXuid(xuidStr);
    }

    public static String generateOnlineXuid() {
        long random = RANDOM.nextLong() & 0x0000FFFFFFFFFFFFL; // 48 bits random
        long xuid = (0x9000L << 48) | random;
        return String.format("0x%016X", xuid);
    }

    // Offline XUID: 0xE000000000000000 | random lower 48 bits
    public static String generateOfflineXuid() {
        long random = RANDOM.nextLong() & 0x0000FFFFFFFFFFFFL; // 48 bits random
        long xuid = (0xE000L << 48) | random;
        return String.format("0x%016X", xuid);
    }

}