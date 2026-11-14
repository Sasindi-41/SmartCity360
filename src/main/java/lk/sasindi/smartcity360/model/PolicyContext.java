
package lk.sasindi.smartcity360.model;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PolicyContext {

    private static final Set<String> ALLOWED_VARIABLES = Set.of(
            "NOISE_LEVEL",
            "PARKING_HOUR",
            "AIR_QUALITY",
            "TRAFFIC_LEVEL"
    );

    public static boolean isAllowedVariable(String variable) {

        return variable != null
                && ALLOWED_VARIABLES.contains(variable.toUpperCase(Locale.ROOT));
    }

    private final Map<String, Double> values;

    public PolicyContext(
            double noiseLevel,
            double parkingHour,
            double airQuality,
            double trafficLevel
    ) {
        this.values = Map.of(
                "NOISE_LEVEL", noiseLevel,
                "PARKING_HOUR", parkingHour,
                "AIR_QUALITY", airQuality,
                "TRAFFIC_LEVEL", trafficLevel
        );
    }

    public double getValue(String variable) {

        if (variable == null) {
            throw new IllegalArgumentException(
                    "Policy variable cannot be null."
            );
        }

        String normalized = variable.toUpperCase(Locale.ROOT);

        if (!ALLOWED_VARIABLES.contains(normalized)) {
            throw new IllegalArgumentException(
                    "Unknown policy variable: " + variable
            );
        }

        return values.get(normalized);
    }

    public Map<String, Double> getValues() {
        return values;
    }
}
