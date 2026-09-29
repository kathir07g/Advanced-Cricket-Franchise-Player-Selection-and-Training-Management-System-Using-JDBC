package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    public static String hash(String password) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            new SecureRandom().nextBytes(salt);
            byte[] hash = generateHash(password.toCharArray(),salt,ITERATIONS);

            return "pbkdf2$" + ITERATIONS + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Password hashing is not available.",e);
        }
    }

    public static boolean matches(String password,String storedPassword) {
        if (password == null || storedPassword == null) {
            return false;
        }
        if (!storedPassword.startsWith("pbkdf2$")) {
            return false;
        }
        try {
            String[] parts = storedPassword.split("\\$");

            if (parts.length != 4) {
                return false;
            }
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = generateHash(password.toCharArray(),salt,iterations);

            if (actual.length != expected.length) {
                return false;
            }

            int result = 0;
            for (int i = 0; i < actual.length; i++) {
                result |= actual[i] ^ expected[i];
            }
            return result == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] generateHash(char[] password,byte[] salt,int iterations) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password,salt,iterations,KEY_LENGTH);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }
}
