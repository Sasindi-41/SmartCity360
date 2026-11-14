package lk.sasindi.smartcity360.patterns.decorator;

import lk.sasindi.smartcity360.dao.EnergyDAO;
import lk.sasindi.smartcity360.model.EnergyStreamContext;
import lk.sasindi.smartcity360.model.StreamProcessingLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.zip.InflaterInputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DecoratorPatternTest {

    private static final String VALID_TOKEN = "valid-meter-token";

    private static final String PAYLOAD =
            "{\"meterId\":1,\"readingValue\":1234.5678,\"unit\":\"kWh\","
                    + "\"note\":\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\"}";

    private static final byte[] KEY = new byte[32];

    private final EnergyDAO fakeDao = new EnergyDAO() {
        @Override
        public boolean verifyMeterToken(int meterId, String token) {
            return VALID_TOKEN.equals(token);
        }
    };

    private EnergyStreamContext context(String token) {
        return new EnergyStreamContext(1, token, PAYLOAD);
    }

    private EnergyStream authenticated() {
        return new AuthenticationDecorator(new BasicEnergyStream(), fakeDao);
    }

    private static byte[] original() {
        return PAYLOAD.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] inflate(byte[] data) throws Exception {
        try (InflaterInputStream in =
                     new InflaterInputStream(new ByteArrayInputStream(data))) {
            return in.readAllBytes();
        }
    }

    private static byte[] decrypt(byte[] data) throws Exception {
        byte[] iv = Arrays.copyOfRange(data, 0, 12);
        byte[] body = Arrays.copyOfRange(data, 12, data.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(
                Cipher.DECRYPT_MODE,
                new SecretKeySpec(KEY, "AES"),
                new GCMParameterSpec(128, iv)
        );

        return cipher.doFinal(body);
    }

    @Test
    @DisplayName("Basic stream only: payload is left unchanged")
    void basicStreamLeavesPayloadUnchanged() throws Exception {

        EnergyStreamContext context = context(VALID_TOKEN);

        new BasicEnergyStream().process(context);

        assertArrayEquals(original(), context.getPayload());
        assertEquals("NONE", context.getCompressionAlgorithm());
        assertEquals("NONE", context.getEncryptionAlgorithm());
    }

    @Test
    @DisplayName("Authentication: a valid token is accepted")
    void validTokenIsAuthenticated() throws Exception {

        EnergyStreamContext context = context(VALID_TOKEN);

        authenticated().process(context);

        assertTrue(context.isAuthenticated());
        assertArrayEquals(original(), context.getPayload());
    }

    @Test
    @DisplayName("Authentication: an invalid token is rejected")
    void invalidTokenIsRejected() {

        EnergyStreamContext context = context("wrong-token");

        assertThrows(
                SecurityException.class,
                () -> authenticated().process(context)
        );

        assertFalse(context.isAuthenticated());

        StreamProcessingLog log = context.getLogs().get(0);

        assertEquals("AUTHENTICATE", log.getOperationName());
        assertFalse(log.isSuccess());
    }

    @Test
    @DisplayName("Compression only: smaller payload that inflates back to the original")
    void compressionOnly() throws Exception {

        EnergyStreamContext context = context(VALID_TOKEN);

        new CompressionDecorator(authenticated()).process(context);

        assertTrue(context.getPayload().length < original().length);
        assertEquals("DEFLATE", context.getCompressionAlgorithm());
        assertArrayEquals(original(), inflate(context.getPayload()));
    }

    @Test
    @DisplayName("Encryption only: ciphertext decrypts back to the original")
    void encryptionOnly() throws Exception {

        EnergyStreamContext context = context(VALID_TOKEN);

        new EncryptionDecorator(authenticated(), KEY).process(context);

        assertEquals("AES/GCM/NoPadding", context.getEncryptionAlgorithm());
        assertFalse(
                new String(context.getPayload(), StandardCharsets.ISO_8859_1)
                        .contains("readingValue")
        );
        assertArrayEquals(original(), decrypt(context.getPayload()));
    }

    @Test
    @DisplayName("Encryption: tampered data is detected (GCM authentication tag)")
    void tamperedCiphertextIsDetected() throws Exception {

        EnergyStreamContext context = context(VALID_TOKEN);

        new EncryptionDecorator(authenticated(), KEY).process(context);

        byte[] data = context.getPayload().clone();
        data[data.length - 1] ^= 0x01;

        assertThrows(AEADBadTagException.class, () -> decrypt(data));
    }

    @Test
    @DisplayName("Encryption: a key that is not 32 bytes is refused")
    void wrongKeyLengthIsRefused() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new EncryptionDecorator(authenticated(), new byte[16])
        );
    }

    @Test
    @DisplayName("All decorators together: authenticate, compress, then encrypt")
    void allDecoratorsTogether() throws Exception {

        EnergyStreamContext context = context(VALID_TOKEN);

        EnergyStream stream = new EncryptionDecorator(
                new CompressionDecorator(authenticated()),
                KEY
        );

        stream.process(context);

        assertArrayEquals(
                original(),
                inflate(decrypt(context.getPayload()))
        );

        List<StreamProcessingLog> logs = context.getLogs();

        assertEquals(3, logs.size());
        assertEquals("AUTHENTICATE", logs.get(0).getOperationName());
        assertEquals("COMPRESS", logs.get(1).getOperationName());
        assertEquals("ENCRYPT", logs.get(2).getOperationName());
    }

    @Test
    @DisplayName("Failed authentication stops the outer decorators")
    void failedAuthenticationStopsOuterDecorators() {

        EnergyStreamContext context = context("wrong-token");

        EnergyStream stream = new EncryptionDecorator(
                new CompressionDecorator(authenticated()),
                KEY
        );

        assertThrows(SecurityException.class, () -> stream.process(context));

        assertEquals("NONE", context.getCompressionAlgorithm());
        assertEquals("NONE", context.getEncryptionAlgorithm());
        assertArrayEquals(original(), context.getPayload());
    }
}
