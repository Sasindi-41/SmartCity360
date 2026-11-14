
package lk.sasindi.smartcity360.model;

import java.time.LocalDateTime;
import java.util.Map;

public class PolicyEvaluation {

    private int id;
    private int policyId;
    private String policyName;
    private String policyExpression;
    private Map<String, Double> contextData;
    private String evaluationResult;
    private String evaluationMessage;
    private LocalDateTime evaluatedAt;

    public PolicyEvaluation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyExpression() {
        return policyExpression;
    }

    public void setPolicyExpression(String policyExpression) {
        this.policyExpression = policyExpression;
    }

    public Map<String, Double> getContextData() {
        return contextData;
    }

    public void setContextData(Map<String, Double> contextData) {
        this.contextData = contextData;
    }

    public String getEvaluationResult() {
        return evaluationResult;
    }

    public void setEvaluationResult(String evaluationResult) {
        this.evaluationResult = evaluationResult;
    }

    public String getEvaluationMessage() {
        return evaluationMessage;
    }

    public void setEvaluationMessage(String evaluationMessage) {
        this.evaluationMessage = evaluationMessage;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }
}
