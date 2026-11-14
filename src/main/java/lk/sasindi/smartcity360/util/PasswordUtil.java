package lk.sasindi.smartcity360.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hashPassword(String password)
            throws GeneralSecurityException {

        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);

        byte[] hash = generateHash(password.toCharArray(), salt);

        return ITERATIONS + ":"
                + Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(
            String password,
            String storedHash
    ) throws GeneralSecurityException {

        if (password == null || storedHash == null) {
            return false;
        }

        String[] parts = storedHash.split(":");

        if (parts.length != 3) {
            return false;
        }

        try {
            int iterations = Integer.parseInt(parts[0]);

            if (iterations < 1 || iterations > 1000000) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

            byte[] actualHash = generateHash(
                    password.toCharArray(),
                    salt,
                    iterations
            );

            return java.security.MessageDigest.isEqual(
                    expectedHash,
                    actualHash
            );

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] generateHash(
            char[] password,
            byte[] salt
    ) throws GeneralSecurityException {

        return generateHash(password, salt, ITERATIONS);
    }

    private static byte[] generateHash(
            char[] password,
            byte[] salt,
            int iterations
    ) throws GeneralSecurityException {

        PBEKeySpec spec = new PBEKeySpec(
                password,
                salt,
                iterations,
                KEY_LENGTH
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            return factory.generateSecret(spec).getEncoded();

        } finally {
            spec.clearPassword();
            java.util.Arrays.fill(password, '\0');
        }
    }
}