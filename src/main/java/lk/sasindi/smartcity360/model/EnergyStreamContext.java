package lk.sasindi.smartcity360.model;

import java.util.ArrayList;
import java.util.List;

public class EnergyStreamContext {

    private final int meterId;
    private final String token;
    private final String originalPayload;

    private byte[] payload;

    private boolean authenticated;
    private String authenticationStatus = "NOT_PERFORMED";

    private String encryptionAlgorithm = "NONE";
    private String compressionAlgorithm = "NONE";

    private final List<StreamProcessingLog> logs = new ArrayList<>();

    public EnergyStreamContext(int meterId,
                               String token,
                               String originalPayload) {
        this.meterId = meterId;
        this.token = token;
        this.originalPayload = originalPayload;
        this.payload = originalPayload.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    public int getMeterId() {
        return meterId;
    }

    public String getToken() {
        return token;
    }

    public String getOriginalPayload() {
        return originalPayload;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        this.authenticationStatus =
                authenticated ? "AUTHENTICATED" : "FAILED";
    }

    public String getAuthenticationStatus() {
        return authenticationStatus;
    }

    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getCompressionAlgorithm() {
        return compressionAlgorithm;
    }

    public void setCompressionAlgorithm(String compressionAlgorithm) {
        this.compressionAlgorithm = compressionAlgorithm;
    }

    public List<StreamProcessingLog> getLogs() {
        return logs;
    }

    public void addLog(String decorator,
                       String operation,
                       boolean success,
                       String details) {
        logs.add(new StreamProcessingLog(
                decorator, operation, success, details
        ));
    }
}