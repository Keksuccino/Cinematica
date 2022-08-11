package de.keksuccino.cinematica.utils;

import java.util.UUID;

public class MiscUtils {

    public static String generateRandomUniqueId() {
        long ms = System.currentTimeMillis();
        String s = UUID.randomUUID().toString();
        return s + ms;
    }

}
