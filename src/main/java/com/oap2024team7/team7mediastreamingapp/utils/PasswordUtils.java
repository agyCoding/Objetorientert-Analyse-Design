package com.oap2024team7.team7mediastreamingapp.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is responsible for hashing the password (using basic SHA-256)
 * This is not secure, bcrypt or scrypt should be used instead, but this is our first project so we're starting with sth easy
 * @author Agata (Agy) Olaussen (@agyCoding)
 */

public class PasswordUtils {

    /**
     * Method to hash the password using SHA-256
     * @param password
     * @return hashed password
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while hashin the password");
        }
    }

    /**
     * Method to convert the byte array to a hex string
     * @param hash
     * @return hex string
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
