package lk.sasindi.smartcity360.model;

import java.util.ArrayList;
import java.util.List;

public class DashboardSummary {
    private int cityCount;
    private int districtCount;
    private int streetCount;
    private int buildingCount;
    private int energyMeterCount;
    private int openComplaintCount;
    private int totalComplaintCount;
    private int streetlightCount;
    private int activePolicyCount;
    private int energyReadingCount;
    private int policyEvaluationCount;
    private String firstName;
    private List<DashboardActivity> recentActivities = new ArrayList<>();
    private List<DashboardChartPoint> energyTrend = new ArrayList<>();
    private List<DashboardChartPoint> complaintStatusBreakdown = new ArrayList<>();
    private List<DashboardChartPoint> buildingDistribution = new ArrayList<>();
    private List<DashboardChartPoint> streetlightModeBreakdown = new ArrayList<>();
    private List<DashboardChartPoint> policyResultBreakdown = new ArrayList<>();

    public int getCityCount() { return cityCount; }
    public void setCityCount(int value) { cityCount = value; }
    public int getDistrictCount() { return districtCount; }
    public void setDistrictCount(int value) { districtCount = value; }
    public int getStreetCount() { return streetCount; }
    public void setStreetCount(int value) { streetCount = value; }
    public int getBuildingCount() { return buildingCount; }
    public void setBuildingCount(int value) { buildingCount = value; }
    public int getEnergyMeterCount() { return energyMeterCount; }
    public void setEnergyMeterCount(int value) { energyMeterCount = value; }
    public int getOpenComplaintCount() { return openComplaintCount; }
    public void setOpenComplaintCount(int value) { openComplaintCount = value; }
    public int getTotalComplaintCount() { return totalComplaintCount; }
    public void setTotalComplaintCount(int value) { totalComplaintCount = value; }
    public int getStreetlightCount() { return streetlightCount; }
    public void setStreetlightCount(int value) { streetlightCount = value; }
    public int getActivePolicyCount() { return activePolicyCount; }
    public void setActivePolicyCount(int value) { activePolicyCount = value; }
    public int getEnergyReadingCount() { return energyReadingCount; }
    public void setEnergyReadingCount(int value) { energyReadingCount = value; }
    public int getPolicyEvaluationCount() { return policyEvaluationCount; }
    public void setPolicyEvaluationCount(int value) { policyEvaluationCount = value; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String value) { firstName = value; }
    public List<DashboardActivity> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<DashboardActivity> value) {
        recentActivities = value == null ? new ArrayList<>() : value;
    }
    public List<DashboardChartPoint> getEnergyTrend() { return energyTrend; }
    public void setEnergyTrend(List<DashboardChartPoint> value) {
        energyTrend = value == null ? new ArrayList<>() : value;
    }
    public List<DashboardChartPoint> getComplaintStatusBreakdown() { return complaintStatusBreakdown; }
    public void setComplaintStatusBreakdown(List<DashboardChartPoint> value) {
        complaintStatusBreakdown = value == null ? new ArrayList<>() : value;
    }
    public List<DashboardChartPoint> getBuildingDistribution() { return buildingDistribution; }
    public void setBuildingDistribution(List<DashboardChartPoint> value) {
        buildingDistribution = value == null ? new ArrayList<>() : value;
    }
    public List<DashboardChartPoint> getStreetlightModeBreakdown() { return streetlightModeBreakdown; }
    public void setStreetlightModeBreakdown(List<DashboardChartPoint> value) {
        streetlightModeBreakdown = value == null ? new ArrayList<>() : value;
    }
    public List<DashboardChartPoint> getPolicyResultBreakdown() { return policyResultBreakdown; }
    public void setPolicyResultBreakdown(List<DashboardChartPoint> value) {
        policyResultBreakdown = value == null ? new ArrayList<>() : value;
    }
}
