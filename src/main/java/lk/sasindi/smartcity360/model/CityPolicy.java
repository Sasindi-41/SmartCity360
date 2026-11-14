
package lk.sasindi.smartcity360.model;

import java.time.LocalDateTime;

public class CityPolicy {

    private int id;
    private String policyName;
    private String policyDescription;
    private String policyExpression;
    private boolean active;
    private LocalDateTime createdAt;

    public CityPolicy() {
    }

    public CityPolicy(
            int id,
            String policyName,
            String policyDescription,
            String policyExpression,
            boolean active,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.policyName = policyName;
        this.policyDescription = policyDescription;
        this.policyExpression = policyExpression;
        this.active = active;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyDescription() {
        return policyDescription;
    }

    public void setPolicyDescription(String policyDescription) {
        this.policyDescription = policyDescription;
    }

    public String getPolicyExpression() {
        return policyExpression;
    }

    public void setPolicyExpression(String policyExpression) {
        this.policyExpression = policyExpression;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
