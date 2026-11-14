package lk.sasindi.smartcity360.model;

public class StreamProcessingLog {

    private String decoratorName;
    private String operationName;
    private boolean success;
    private String details;

    public StreamProcessingLog(String decoratorName,
                               String operationName,
                               boolean success,
                               String details) {
        this.decoratorName = decoratorName;
        this.operationName = operationName;
        this.success = success;
        this.details = details;
    }

    public String getDecoratorName() {
        return decoratorName;
    }

    public String getOperationName() {
        return operationName;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getDetails() {
        return details;
    }
}