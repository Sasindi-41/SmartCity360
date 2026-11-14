package lk.sasindi.smartcity360.patterns.chain;

import java.util.Locale;

public final class EscalationPolicy {

    private EscalationPolicy() {
    }

    public static int requiredLevelFor(String priorityName) {

        if (priorityName == null) {
            throw new IllegalArgumentException(
                    "Unsupported priority: null"
            );
        }

        return switch (priorityName.trim().toLowerCase(Locale.ROOT)) {
            case "low" -> EscalationTier.LOCAL_STAFF.getSequenceNumber();
            case "medium" -> EscalationTier.SUPERVISOR.getSequenceNumber();
            case "high", "critical" ->
                    EscalationTier.CITY_MANAGER.getSequenceNumber();
            default -> throw new IllegalArgumentException(
                    "Unsupported priority: " + priorityName
            );
        };
    }
}
