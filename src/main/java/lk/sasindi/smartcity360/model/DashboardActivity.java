package lk.sasindi.smartcity360.model;

public class DashboardActivity {

    private final String type;
    private final String title;
    private final String description;
    private final String timeLabel;

    public DashboardActivity(String type, String title,
                             String description, String timeLabel) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.timeLabel = timeLabel;
    }

    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTimeLabel() { return timeLabel; }
}
