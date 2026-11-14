
package lk.sasindi.smartcity360.model;

import java.util.ArrayList;
import java.util.List;

public class EnergyProcessResult {

    private boolean success;
    private String message;
    private int streamRecordId;

    private String serialNumber;
    private String receivedAt;

    private String authenticationStatus;
    private String processingStatus;

    private String encryptionAlgorithm;
    private String compressionAlgorithm;

    private double readingValue;
    private String unit;

    private String processedPayload;

    private long originalSize;
    private long finalSize;

    private final List<StreamProcessingLog> logs = new ArrayList<>();

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStreamRecordId() {
        return streamRecordId;
    }

    public void setStreamRecordId(int streamRecordId) {
        this.streamRecordId = streamRecordId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getAuthenticationStatus() {
        return authenticationStatus;
    }

    public void setAuthenticationStatus(String authenticationStatus) {
        this.authenticationStatus = authenticationStatus;
    }

    public String getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
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

    public double getReadingValue() {
        return readingValue;
    }

    public void setReadingValue(double readingValue) {
        this.readingValue = readingValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProcessedPayload() {
        return processedPayload;
    }

    public void setProcessedPayload(String processedPayload) {
        this.processedPayload = processedPayload;
    }

    public long getOriginalSize() {
        return originalSize;
    }

    public void setOriginalSize(long originalSize) {
        this.originalSize = originalSize;
    }

    public long getFinalSize() {
        return finalSize;
    }

    public void setFinalSize(long finalSize) {
        this.finalSize = finalSize;
    }

    public List<StreamProcessingLog> getLogs() {
        return logs;
    }

    public void addLogs(List<StreamProcessingLog> values) {
        if (values != null) {
            logs.addAll(values);
        }
    }
}
