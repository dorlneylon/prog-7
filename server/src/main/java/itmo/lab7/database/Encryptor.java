package itmo.lab7.database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryptor {
    private static final MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encrypts the given stringToEncrypt string
     *
     * @param stringToEncrypt the string to be encrypted
     * @return the encrypted string
     */
    public static String encryptString(String stringToEncrypt) {
        byte[] encryptedString = messageDigest.digest(stringToEncrypt.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encryptedString);
    }

    /**
     * Compares two hashes to see if they are equal
     *
     * @param stringToEncrypt The string to be encrypted
     * @param encryptedString The encrypted string to compare
     * @return true if the two hashes are equal, false otherwise
     */
    public static boolean compareHashes(String stringToEncrypt, String encryptedString) {
        return bytesToHex(messageDigest.digest(stringToEncrypt.getBytes(StandardCharsets.UTF_8))).equals(encryptedString);
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param encryptedString The byte array to convert.
     * @return The hexadecimal string representation of the byte array.
     */
    private static String bytesToHex(byte[] encryptedString) {
        StringBuilder hexadecimalString = new StringBuilder(2 * encryptedString.length);
        for (byte byte_ : encryptedString) {
            String hexadecimal = Integer.toHexString(0xff & byte_);
            if (hexadecimal.length() == 1) {
                hexadecimalString.append('0');
            }
            hexadecimalString.append(hexadecimal);
        }
        return hexadecimalString.toString();
    }
}
