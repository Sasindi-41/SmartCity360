package lk.sasindi.smartcity360.patterns.decorator;

import lk.sasindi.smartcity360.model.EnergyStreamContext;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionDecorator extends EnergyStreamDecorator {

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private final SecretKeySpec secretKey;

    public EncryptionDecorator(EnergyStream wrappedStream, byte[] key) {
        super(wrappedStream);

        if (key == null || key.length != 32) {
            throw new IllegalArgumentException(
                    "AES-256 requires a 32-byte key."
            );
        }

        this.secretKey = new SecretKeySpec(key, "AES");
    }

    @Override
    public void process(EnergyStreamContext context) throws Exception {

        super.process(context);

        try {
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    new GCMParameterSpec(TAG_LENGTH, iv)
            );

            byte[] encrypted = cipher.doFinal(context.getPayload());

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(iv);
            output.write(encrypted);

            context.setPayload(output.toByteArray());
            context.setEncryptionAlgorithm("AES/GCM/NoPadding");

            context.addLog(
                    "EncryptionDecorator",
                    "ENCRYPT",
                    true,
                    "Energy stream encrypted using AES-256-GCM."
            );

        } catch (Exception exception) {

            context.addLog(
                    "EncryptionDecorator",
                    "ENCRYPT",
                    false,
                    "Encryption failed: " +
                            (exception.getMessage() == null
                                    ? "Unknown encryption error"
                                    : exception.getMessage())
            );

            throw exception;
        }
    }
}