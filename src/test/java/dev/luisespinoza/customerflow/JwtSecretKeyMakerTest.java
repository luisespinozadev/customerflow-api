package dev.luisespinoza.customerflow;

import org.junit.jupiter.api.Test;
import java.security.SecureRandom;
import java.util.Base64;

public class JwtSecretKeyMakerTest {

    @Test
    public void generateSecretKey() {
        byte[] key = new byte[32]; // 256 bits
        new SecureRandom().nextBytes(key);

        String base64Key = Base64.getEncoder().withoutPadding().encodeToString(key);

        System.out.println("JWT_SECRET_KEY=" + base64Key);
    }
}
