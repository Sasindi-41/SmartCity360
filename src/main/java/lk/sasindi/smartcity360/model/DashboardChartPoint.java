package lk.sasindi.smartcity360.model;

public class DashboardChartPoint {
    private final String label;
    private final long value;

    public DashboardChartPoint(String label, long value) {
        this.label = label == null ? "Unspecified" : label;
        this.value = value;
    }

    public String getLabel() { return label; }
    public long getValue() { return value; }
}
