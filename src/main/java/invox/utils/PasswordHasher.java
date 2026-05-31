package invox.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hashing simplu de parole (SHA-256), ca sa nu stocam parola in clar.
 * Pentru un proiect e suficient; intr-o aplicatie reala s-ar folosi bcrypt/argon2 cu salt.
 */
public final class PasswordHasher {

    private PasswordHasher() {
    }

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 indisponibil", e);
        }
    }
}
