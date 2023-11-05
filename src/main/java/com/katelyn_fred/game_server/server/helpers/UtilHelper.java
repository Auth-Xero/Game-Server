package com.katelyn_fred.game_server.server.helpers;

import java.util.Random;

public class UtilHelper {
    public static String generateID(int length) {
        String availableChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) {
            int index = (int) (rnd.nextFloat() * availableChars.length());
            salt.append(availableChars.charAt(index));
        }
        return salt.toString();
    }
}
